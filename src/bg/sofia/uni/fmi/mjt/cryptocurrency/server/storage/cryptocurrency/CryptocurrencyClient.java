package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class CryptocurrencyClient implements Runnable {
    //paste-your-API-key-here
    private static final String API_KEY = "D9FB62E2-FF6B-41BF-B8D9-C3A14B40CF92";
    private static final String API_KEY_HEADER_NAME = "X-CoinAPI-Key";
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "rest.coinapi.io";
    private static final String API_ENDPOINT_PATH = "/v1/assets";
    private static final Gson GSON = new Gson();
//    private static final int MAX_RESPONSE_SIZE = 50;
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
            throw new RuntimeException("Could not retrieve cryptocurrencies", e);
        }

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            Type type = new TypeToken<List<Cryptocurrency>>() {
            }.getType();

            List<Cryptocurrency> receivedCryptocurrencies = GSON.fromJson(response.body(), type);
            Map<String, Cryptocurrency> receivedCryptocurrenciesMap = new HashMap<>();

            receivedCryptocurrencies.stream()
                .filter(cryptocurrency -> cryptocurrency.isCrypto() == 1)
                .forEach(cryptocurrency -> receivedCryptocurrenciesMap.put(cryptocurrency.assetId(), cryptocurrency));

            notifyObservers(receivedCryptocurrenciesMap);

        } else {
            throw new RuntimeException("nothing");
        }

        //handleErrorStatusCodes(response.statusCode());
    }

    public void registerObserver(CryptocurrencyObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Map<String, Cryptocurrency> receivedCryptocurrenciesMap) {
        for (CryptocurrencyObserver observer : observers) {
            observer.update(receivedCryptocurrenciesMap);
        }
    }


//    private void handleErrorStatusCodes(int statusCode) throws CryptocurrenciesRequestHandlerException {
//        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
//            throw new WrongRequestException("There is something wrong with your request");
//        }
//
//        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//            throw new UnauthorizedApiKeyException("API key is wrong");
//        }
//
//        if (statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
//            throw new ForbiddenResourseException("API key doesnt't have enough privileges to access this resource");
//        }
//
//        throw new CryptocurrenciesRequestHandlerException("Unexpected response code from cryptocurrency service");
//    }
}
