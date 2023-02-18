package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class CryptocurrencyClientException extends RuntimeException {
    public CryptocurrencyClientException(String message) {
        super(message);
    }

    public CryptocurrencyClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
