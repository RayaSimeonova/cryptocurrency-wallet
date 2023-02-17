package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class CryptocurrencyNotFoundException extends Exception {
    public CryptocurrencyNotFoundException(String message) {
        super(message);
    }

    public CryptocurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
