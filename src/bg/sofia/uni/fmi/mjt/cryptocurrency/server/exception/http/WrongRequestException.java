package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class WrongRequestException extends CryptocurrencyClientException {
    public WrongRequestException(String message) {
        super(message);
    }

    public WrongRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
