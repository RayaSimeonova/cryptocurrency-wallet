package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class ForbiddenResourceException extends CryptocurrencyClientException {
    public ForbiddenResourceException(String message) {
        super(message);
    }

    public ForbiddenResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
