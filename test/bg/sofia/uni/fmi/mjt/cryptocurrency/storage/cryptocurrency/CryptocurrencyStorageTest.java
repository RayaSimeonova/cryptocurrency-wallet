package bg.sofia.uni.fmi.mjt.cryptocurrency.storage.cryptocurrency;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailableException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptocurrencyStorageTest {

    private static CryptocurrencyStorage storage;

    @BeforeAll
    static void setUp() {
        storage = new CryptocurrencyStorage();
    }

    @Test
    void testGetAllCryptocurrenciesForCryptocurrencyUnavailable() {
        assertThrows(CryptocurrencyUnavailableException.class, () -> storage.getAllCryptocurrencies(),
            "Method should throw CryptocurrencyUnavailableException when trying to access the storage before " +
                "cryptocurrencies' information is uploaded");
    }

    @Test
    void testGetCryptocurrencyForCryptocurrencyUnavailable() {
        assertThrows(CryptocurrencyUnavailableException.class, () -> storage.getCryptocurrency("BTC"),
            "Method should throw CryptocurrencyUnavailableException when trying to access the storage before " +
                "cryptocurrencies' information is uploaded");
    }

    @Test
    void testGetCryptocurrencyForNotFoundCryptocurrency() {
        Cryptocurrency btc = new Cryptocurrency("BTC", "Bitcoin", 1, 22711,
            102894431436.49, 2086392323256.16, 57929168359984.54,
            9166.207274778093436220194944);
        Map<String, Cryptocurrency> updatedCryptocurrencies = Map.of("BTC", btc);
        storage.update(updatedCryptocurrencies);

        assertThrows(CryptocurrencyNotFoundException.class, () -> storage.getCryptocurrency("ray"),
            "Method should throw CryptocurrencyNotFoundException when trying to access the storage before " +
                "cryptocurrencies' information is uploaded");
    }

    @Test
    void testGetCryptocurrencyForValidCryptocurrencyWithIgnoreCase()
        throws CryptocurrencyNotFoundException, CryptocurrencyUnavailableException {
        Cryptocurrency btc = new Cryptocurrency("BTC", "Bitcoin", 1, 22711,
            102894431436.49, 2086392323256.16, 57929168359984.54,
            9166.207274778093436220194944);
        Map<String, Cryptocurrency> updatedCryptocurrencies = Map.of("BTC", btc);
        storage.update(updatedCryptocurrencies);

        assertEquals(btc, storage.getCryptocurrency("btc"),
            "Method should return the accurate cryptocurrency for cryptoId (ignoring case)");
    }
}
