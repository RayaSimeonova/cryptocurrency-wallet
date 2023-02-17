package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(String message) {
        super(message);
    }

    public NotEnoughMoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}
