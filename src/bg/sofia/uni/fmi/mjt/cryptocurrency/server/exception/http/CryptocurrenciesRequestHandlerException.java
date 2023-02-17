package bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http;

public class CryptocurrenciesRequestHandlerException
    extends RuntimeException {
    public CryptocurrenciesRequestHandlerException(String message) {
        super(message);
    }

    public CryptocurrenciesRequestHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
