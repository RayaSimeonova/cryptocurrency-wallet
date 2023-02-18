package bg.sofia.uni.fmi.mjt.cryptocurrency.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final int SERVER_PORT = 3456;
    private static final String SERVER_HOSTNAME = "localhost";
    private static final String STOP_CLIENT_COMMAND = "disconnect";
    private static final String STOP_READING_SERVER_REPLY_MARK = "--finished--";

    public void start() {

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT));

            while (true) {
                System.out.print("Enter command: ");
                String command = scanner.nextLine(); // read a line from the console
                writer.println(command);

                String reply;
                while (!(reply = reader.readLine()).equals(STOP_READING_SERVER_REPLY_MARK)) {
                    System.out.println(reply);
                }

                if (STOP_CLIENT_COMMAND.equalsIgnoreCase(command)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to the server. Please try again later.");
        }
    }

}
