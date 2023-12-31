package bg.sofia.uni.fmi.mjt.cryptocurrency.server;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.Command;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CommandArgumentsException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.CryptocurrencyClientException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyClient;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryUserStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.UserStorage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    private static final int BUFFER_SIZE = 10_240;
    private static final String OUTPUT_END = System.lineSeparator() + "--finished--" + System.lineSeparator();
    private static final int PORT = 3456;
    private static final String HOST = "localhost";
    private static final String DB_FILENAME = "db.json";
    private static final String LOG_FILENAME = "cryptocurrency.log";
    private final CommandExecutor commandExecutor;
    private final UserStorage userStorage;
    private final CryptocurrencyStorage cryptocurrencyStorage;
    private final ScheduledExecutorService scheduler;

    private static final int CRYPTOCURRENCIES_UPDATE_PERIOD = 30;
    private static final int CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY = 0;

    private final Logger logger;
    private final AtomicBoolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    private boolean isUserStorageForSaving;

    public Server() {
        userStorage = new InMemoryUserStorage();
        cryptocurrencyStorage = new CryptocurrencyStorage();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        isServerWorking = new AtomicBoolean();
        isUserStorageForSaving = true;

        logger = ConfiguredLoggerCreator.newLogger(Server.class.getName(), LOG_FILENAME);
        configCryptocurrencyClient();

        this.commandExecutor = new CommandExecutor(userStorage, cryptocurrencyStorage, logger);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking.set(true);
            configUserStorage();

            while (isServerWorking.get()) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            String clientInput = getClientInput(clientChannel);
                            if (clientInput == null) {
                                continue;
                            }
                            writeClientOutput(clientChannel, getServerReply(clientChannel, clientInput));
                        } else if (key.isAcceptable()) {
                            accept(selector, key);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            saveUserStorage();
            scheduler.shutdown();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start server", e);
        }
    }

    public void stop() {
        this.isServerWorking.set(false);
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void saveUserStorage() {
        if (isUserStorageForSaving) {
            try (Writer writer = new FileWriter(DB_FILENAME)) {
                userStorage.uploadToDB(writer);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Couldn't save user storage to the database file.", e);
            }
        }
    }

    private void configUserStorage() {
        try (Reader reader = new FileReader(DB_FILENAME)) {
            userStorage.uploadFromDB(reader);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Database file not found.", e);
            isUserStorageForSaving = false;
            stop();
//            File yourFile = new File("score.txt");
//            yourFile.createNewFile(); // if file already exists will do nothing
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't load database.", e);
            isUserStorageForSaving = false;
            stop();        }
    }

    private void configCryptocurrencyClient() {
        final var httpClient = HttpClient.newBuilder().build();
        final var cryptocurrencyClient = new CryptocurrencyClient(httpClient);
        cryptocurrencyClient.registerObserver(cryptocurrencyStorage);
        try {
            scheduler.scheduleAtFixedRate(cryptocurrencyClient, CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY,
                CRYPTOCURRENCIES_UPDATE_PERIOD, TimeUnit.MINUTES);
        } catch (CryptocurrencyClientException e) {
            logger.log(Level.SEVERE, "Problem with HTTP communication", e);
//            stop();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private String getServerReply(SocketChannel clientChannel, String clientInput) {
        try {
            Command command = CommandCreator.newCommand(clientInput, commandExecutor);
            return command.execute(clientChannel.getRemoteAddress());
        } catch (NumberFormatException e) {
            return "Invalid input for expected number argument" + OUTPUT_END;
        } catch (CommandArgumentsException | IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (IOException e) {
            logger.log(Level.INFO, String.format("User client channel: < %s > %s",
                clientChannel, System.lineSeparator() + e.getMessage()), e);
            return "Server could not identify you while trying to process your input. Try again later, please!"
                + OUTPUT_END;
        }
    }
    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}