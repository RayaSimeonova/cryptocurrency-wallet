package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class ForbiddenResourseException extends CryptocurrenciesRequestHandlerException {
    public ForbiddenResourseException(String message) {
        super(message);
    }

    public ForbiddenResourseException(String message, Throwable cause) {
        super(message, cause);
    }
}
