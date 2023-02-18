package bg.sofia.uni.fmi.mjt.cryptocurrency.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailableException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.CryptocurrencyClientException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.ForbiddenResourceException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.UnauthorizedApiKeyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.WrongRequestException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyClient;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyObserver;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CryptocurrencyClientTest {
    @Mock
    private static HttpClient httpClientMock;

    @Mock
    private static HttpResponse<String> httpResponseMock;

    private CryptocurrencyClient client;

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);

    }

    @BeforeEach
    public void setUpCase() throws IOException, InterruptedException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(httpResponseMock);

        client = new CryptocurrencyClient(httpClientMock);
    }

    @Test
    void testCryptocurrencyClientForBadRequestException() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        assertThrows(WrongRequestException.class, () -> client.run(),
            "Method should throw WrongRequestException when trying to send invalid http request.");
    }

    @Test
    void testCryptocurrencyClientForUnauthorizedApiKeyException() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        assertThrows(UnauthorizedApiKeyException.class, () -> client.run(),
            "Method should throw UnauthorizedApiKeyException when trying to send http request with invalid API key.");
    }

    @Test
    void testCryptocurrencyClientForForbiddenResourceException() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);

        assertThrows(ForbiddenResourceException.class, () -> client.run(),
            "Method should throw ForbiddenResourceException when trying to access resource with API key that " +
                "does not have clearance.");
    }

    @Test
    void testCryptocurrencyClientForServerError() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAVAILABLE);

        assertThrows(CryptocurrencyClientException.class, () -> client.run(),
            "Method should throw CryptocurrencyClientException when unsuccessful in retrieving the cryptocurrencies.");
    }

    @Test
    void testCryptocurrencyClientForIOException() throws IOException, InterruptedException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenThrow(new IOException());

        assertThrows(CryptocurrencyClientException.class, () -> client.run(),
            "Method should throw CryptocurrencyClientException when an IOException or InterruptedException occurs.");
    }

    @Test
    void testRunWithValidRequest() throws CryptocurrencyUnavailableException {
        Cryptocurrency btc = new Cryptocurrency("BTC", "Bitcoin", 1, 22711,
            102894431436.49, 2086392323256.16, 57929168359984.54,
            9166.207274778093436220194944);
        List<Cryptocurrency> cryptocurrencyList = List.of(btc);
        String cryptocurrencyListJson = new Gson().toJson(cryptocurrencyList);

        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpResponseMock.body()).thenReturn(cryptocurrencyListJson);

        CryptocurrencyObserver observer = new CryptocurrencyStorage();
        client.registerObserver(observer);

        client.run();
        assertIterableEquals(cryptocurrencyList, observer.getAllCryptocurrencies(),
            "Method should notify observers about the updated cryptocurrencies");

    }
}
