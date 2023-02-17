package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailable;

import java.net.http.HttpClient;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CryptocurrencyStorage implements CryptocurrencyObserver {
//    private static final int CRYPTOCURRENCIES_UPDATE_PERIOD = 30;
//    private static final int CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY = 0;
//    private final ScheduledExecutorService scheduler;
//    private final CryptocurrencyClient cryptocurrencyClient;
//    private final HttpClient httpClient;
    private static final int MAX_CRYPTOCURRENCY_COUNT_TO_DISPLAY = 50;
    private final Map<String, Cryptocurrency> cryptocurrencies;

    public CryptocurrencyStorage() {

        cryptocurrencies = Collections.synchronizedMap(new HashMap<>());
//        httpClient = HttpClient.newBuilder().build();

//        cryptocurrencyClient = new CryptocurrencyClient(httpClient);
//        cryptocurrencyClient.registerObserver(this);

//        this.scheduler = scheduler;
//        this.scheduler.scheduleAtFixedRate(cryptocurrencyClient, CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY,
//            CRYPTOCURRENCIES_UPDATE_PERIOD, TimeUnit.MINUTES);
    }

    public Cryptocurrency getCryptocurrency(String cryptoId)
        throws CryptocurrencyNotFoundException, CryptocurrencyUnavailable {
        isActive();
        if (!cryptocurrencies.containsKey(cryptoId)) {
            throw new CryptocurrencyNotFoundException(String.format("Cryptocurrency %s is not available.", cryptoId));
        }
        return cryptocurrencies.get(cryptoId);
    }

    public Collection<Cryptocurrency> getAllCryptocurrencies() throws CryptocurrencyUnavailable {
        isActive();
        return cryptocurrencies.values().stream().limit(MAX_CRYPTOCURRENCY_COUNT_TO_DISPLAY).toList();
    }

    @Override
    public void update(Map<String, Cryptocurrency> updatedCryptocurrencies) {
        cryptocurrencies.putAll(updatedCryptocurrencies);
    }

    private void isActive() throws CryptocurrencyUnavailable {
        if (cryptocurrencies.isEmpty()) {
            throw new CryptocurrencyUnavailable("Cryptocurrencies are not uploaded to storage.");
        }
    }
}
