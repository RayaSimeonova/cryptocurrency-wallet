package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command;

public class CryptocurrencyUnavailable extends Exception {
    public CryptocurrencyUnavailable(String message) {
        super(message);
    }

    public CryptocurrencyUnavailable(String message, Throwable cause) {
        super(message, cause);
    }
}
