package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailableException;

import java.util.Collection;
import java.util.Map;

public interface CryptocurrencyObserver {
    /***
     *
     * @param updatedCryptocurrencies
     *
     */
    void update(Map<String, Cryptocurrency> updatedCryptocurrencies);

    Collection<Cryptocurrency> getAllCryptocurrencies() throws CryptocurrencyUnavailableException;
}
