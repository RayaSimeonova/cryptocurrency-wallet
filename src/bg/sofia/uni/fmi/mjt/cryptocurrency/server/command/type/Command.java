package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;


import java.net.SocketAddress;

public interface Command {
    /***
     *
     * @param userAddress the socket address of the client
     * @return message indicating the result of the command's execution
     */
    String execute(SocketAddress userAddress);
}
