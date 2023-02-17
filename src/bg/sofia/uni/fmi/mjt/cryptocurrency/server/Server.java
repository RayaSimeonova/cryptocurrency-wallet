package bg.sofia.uni.fmi.mjt.cryptocurrency.server;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.Command;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.CryptocurrenciesRequestHandlerException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.FileHandler;
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
    private final InMemoryStorage inMemoryStorage;
    private final CryptocurrencyStorage cryptocurrencyStorage;
    private final ScheduledExecutorService scheduler;

    private final Logger logger;
    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    public Server() {
        inMemoryStorage = new InMemoryStorage();

        try {
            logger = Logger.getLogger(Server.class.getName());
            logger.addHandler(new FileHandler(LOG_FILENAME));
        } catch (IOException e) {
            throw new UncheckedIOException("failed to open the log file", e);
        } catch (SecurityException e) {
            throw new RuntimeException("No security permissions", e);
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            cryptocurrencyStorage = new CryptocurrencyStorage(scheduler);
        } catch (CryptocurrenciesRequestHandlerException e) {
            logger.log(Level.SEVERE, "Problem with HTTP communication", e);
            throw new RuntimeException("Problem with HTTP communication", e);
        }
        this.commandExecutor = new CommandExecutor(inMemoryStorage, cryptocurrencyStorage, logger);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
            isServerWorking = true;
            configInMemoryStorage();

            while (isServerWorking) {
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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "failed to start server", e);
        }
    }

    public void stop() {
        saveUserStorage();
        stopWithoutUpdatingDB();
    }

    private void stopWithoutUpdatingDB() {
        scheduler.shutdown();
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void saveUserStorage() {
        try (Writer writer = new FileWriter(DB_FILENAME)) {
            inMemoryStorage.uploadToDB(writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't save user storage to the database file.", e);
        }
    }

    private void configInMemoryStorage() {
        try (Reader reader = new FileReader(DB_FILENAME)) {
            inMemoryStorage.uploadFromDB(reader);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Database file not found.", e);
            stopWithoutUpdatingDB();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't load database.", e);
            stopWithoutUpdatingDB();
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
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage(), e);
            return e.getMessage() + OUTPUT_END;
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