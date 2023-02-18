package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class CryptocurrencyUnavailableException extends Exception {
    public CryptocurrencyUnavailableException(String message) {
        super(message);
    }

    public CryptocurrencyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
