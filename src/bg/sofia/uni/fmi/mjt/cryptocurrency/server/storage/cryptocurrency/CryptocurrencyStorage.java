package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailable;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class CryptocurrencyStorage implements CryptocurrencyObserver {
    private static final int MAX_CRYPTOCURRENCY_COUNT_TO_DISPLAY = 50;
    private final Map<String, Cryptocurrency> cryptocurrencies;

    public CryptocurrencyStorage() {
        cryptocurrencies = Collections.synchronizedMap(new HashMap<>());
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
