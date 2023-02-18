package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;

import java.util.Map;

public interface CryptocurrencyObserver {
    /***
     *
     * @param updatedCryptocurrencies
     *
     */
    void update(Map<String, Cryptocurrency> updatedCryptocurrencies);
}
