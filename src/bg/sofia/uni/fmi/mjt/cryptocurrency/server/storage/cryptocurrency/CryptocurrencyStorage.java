package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;

import java.net.http.HttpClient;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptocurrencyStorage {
    private static final int CRYPTOCURRENCIES_UPDATE_PERIOD = 30;
    private static final int CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY = 0;
    private final ScheduledExecutorService scheduler;
    private final CryptocurrenciesRequestHandler cryptocurrenciesRequestHandler;
    private final Map<String, Cryptocurrency> cryptocurrencies;
    private final HttpClient cryptocurrenciesClient;

    public CryptocurrencyStorage(ScheduledExecutorService scheduler) {

        cryptocurrencies = Collections.synchronizedMap(new HashMap<>());
        cryptocurrenciesClient = HttpClient.newBuilder().build();

        cryptocurrenciesRequestHandler =
            new CryptocurrenciesRequestHandler(cryptocurrencies, cryptocurrenciesClient);

        this.scheduler = scheduler;
        this.scheduler.scheduleAtFixedRate(cryptocurrenciesRequestHandler, CRYPTOCURRENCIES_UPDATE_INITIAL_DELAY,
            CRYPTOCURRENCIES_UPDATE_PERIOD, TimeUnit.MINUTES);
    }

    public Cryptocurrency getCryptocurrency(String cryptoId) throws CryptocurrencyNotFoundException {
        if (!cryptocurrencies.containsKey(cryptoId)) {
            throw new CryptocurrencyNotFoundException(String.format("Cryptocurrency %s is not available.", cryptoId));
        }
        return cryptocurrencies.get(cryptoId);
    }

    public Collection<Cryptocurrency> getAllCryptocurrencies() {
        return cryptocurrencies.values();
    }

}
