package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class CommandArgumentsException extends Exception {
    public CommandArgumentsException(String message) {
        super(message);
    }

    public CommandArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
