package bg.sofia.uni.fmi.mjt.cryptocurrency.server;

public class ServerMain {

    public static void main(String[] args) {

        var server = new Server();
        var serverScanner = new Thread(new ServerStopScanner(server));

        serverScanner.start();
        server.start();

        //api key env var / arg
    }

}
