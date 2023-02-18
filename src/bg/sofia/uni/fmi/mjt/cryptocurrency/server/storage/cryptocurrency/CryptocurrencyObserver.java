package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailableException;

import java.util.Collection;
import java.util.Map;

public interface CryptocurrencyObserver {
    /***
     * Updates the cryptocurrencies local data with the new one received
     *
     * @param updatedCryptocurrencies the new cryptocurrencies data
     *
     */
    void update(Map<String, Cryptocurrency> updatedCryptocurrencies);

    /***
     *
     * @return a collection of all available cryptocurrencies
     * @throws CryptocurrencyUnavailableException if the collection is empty
     */
    Collection<Cryptocurrency> getAllCryptocurrencies() throws CryptocurrencyUnavailableException;
}
