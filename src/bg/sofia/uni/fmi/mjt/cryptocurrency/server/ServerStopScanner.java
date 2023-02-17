package bg.sofia.uni.fmi.mjt.cryptocurrency.server;

import java.util.Scanner;

public class ServerStopScanner implements Runnable {
    private static final String STOP_WORD = "stop";
    private final Scanner scanner;
    private final Server server;

    public ServerStopScanner(Server server) {
        scanner = new Scanner(System.in);
        this.server = server;
    }

    @Override
    public void run() {
        String serverInput;

        do {
            serverInput = scanner.nextLine();
        } while (!serverInput.equalsIgnoreCase(STOP_WORD));

        server.stop();
    }
}
