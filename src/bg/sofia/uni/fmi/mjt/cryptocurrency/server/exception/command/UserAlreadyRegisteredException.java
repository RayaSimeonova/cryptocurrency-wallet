package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class UserAlreadyRegisteredException extends Exception {

    public UserAlreadyRegisteredException(String message) {
        super(message);
    }

    public UserAlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
