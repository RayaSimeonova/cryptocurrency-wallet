package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.CryptocurrencyClientException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.ForbiddenResourceException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.UnauthorizedApiKeyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.http.WrongRequestException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CryptocurrencyClient implements Runnable {
    // paste-your-API-key-here
    private static final String API_KEY = "D9FB62E2-FF6B-41BF-B8D9-C3A14B40CF92";
    private static final String API_KEY_HEADER_NAME = "X-CoinAPI-Key";
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "rest.coinapi.io";
    private static final String API_ENDPOINT_PATH = "/v1/assets";
    private static final Gson GSON = new Gson();
    private final HttpClient cryptocurrencyHttpClient;
    private final List<CryptocurrencyObserver> observers;


    public CryptocurrencyClient(HttpClient client) {
        this.cryptocurrencyHttpClient = client;
        observers = new ArrayList<>();
    }

    @Override
    public void run() {
        HttpResponse<String> response;
        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header(API_KEY_HEADER_NAME, API_KEY).build();

            response = cryptocurrencyHttpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            throw new CryptocurrencyClientException("Could not retrieve cryptocurrencies", e);
        }

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {

            var cryptocurrencyMap = getProcessedResponse(response);
            notifyObservers(cryptocurrencyMap);

        } else {
            handleErrorStatusCodes(response.statusCode());
        }
    }

    public void registerObserver(CryptocurrencyObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Map<String, Cryptocurrency> receivedCryptocurrenciesMap) {
        for (CryptocurrencyObserver observer : observers) {
            observer.update(receivedCryptocurrenciesMap);
        }
    }

    private Map<String, Cryptocurrency> getProcessedResponse(HttpResponse<String> response) {
        Type type = new TypeToken<List<Cryptocurrency>>() {
        }.getType();

        List<Cryptocurrency> cryptocurrencies = GSON.fromJson(response.body(), type);
        Map<String, Cryptocurrency> cryptocurrencyMap = new HashMap<>();

        cryptocurrencies.stream()
            .filter(cryptocurrency -> cryptocurrency.isCrypto() == 1)
            .forEach(cryptocurrency -> cryptocurrencyMap.put(cryptocurrency.assetId(), cryptocurrency));

        return cryptocurrencyMap;
    }

    private void handleErrorStatusCodes(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new WrongRequestException("There is something wrong with the request");
        }

        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new UnauthorizedApiKeyException("API key is wrong");
        }

        if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            throw new ForbiddenResourceException("API key doesn't have enough privileges to access this resource");
        }

        throw new CryptocurrencyClientException("Unexpected response code from the cryptocurrency service");
    }
}
