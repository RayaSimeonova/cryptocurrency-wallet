package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class UserNotLoggedException extends Exception {

    public UserNotLoggedException(String message) {
        super(message);
    }

    public UserNotLoggedException(String message, Throwable cause) {
        super(message, cause);
    }
}
