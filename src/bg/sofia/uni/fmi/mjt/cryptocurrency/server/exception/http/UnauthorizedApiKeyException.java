package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class UnauthorizedApiKeyException
    extends CryptocurrencyClientException {
    public UnauthorizedApiKeyException(String message) {
        super(message);
    }

    public UnauthorizedApiKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
