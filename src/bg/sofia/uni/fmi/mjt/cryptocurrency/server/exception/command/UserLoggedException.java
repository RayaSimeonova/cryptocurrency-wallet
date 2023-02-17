package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class UserLoggedException extends Exception {
    public UserLoggedException(String message) {
        super(message);
    }

    public UserLoggedException(String message, Throwable cause) {
        super(message, cause);
    }
}
