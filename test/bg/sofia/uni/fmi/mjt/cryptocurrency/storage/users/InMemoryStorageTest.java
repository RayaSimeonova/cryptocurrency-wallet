package bg.sofia.uni.fmi.mjt.cryptocurrency.storage.users;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.StockInfo;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.NotEnoughMoneyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryStorageTest {
    private InMemoryStorage inMemoryStorage;
    private static User userInStorage;
    private static User userUnexisting;
    private static Cryptocurrency cryptocurrency;

    @BeforeAll
    static void setUp() throws NoSuchAlgorithmException {
        userInStorage = new User("John", "island");
        userUnexisting = new User("Jane", "inTheStream");
        //inMemoryStorage = new InMemoryStorage();
        //inMemoryStorage.addUser(userInStorage);
        cryptocurrency = new Cryptocurrency("BTC", "Bitcoin", 1, 22711,
            102894431436.49, 2086392323256.16, 57929168359984.54,
            9166.207274778093436220194944);
    }

    @BeforeEach
    void setUpCase() throws UserAlreadyRegisteredException {
        inMemoryStorage = new InMemoryStorage();
        inMemoryStorage.addUser(userInStorage);
    }

    @Test
    void testAddUserOk() {
        //inMemoryStorage.addUser(userInStorage);
        assertTrue(inMemoryStorage.containsUser(userInStorage), "Method should add unregistered user to storage");
    }

    @Test
    void testAddUserAlreadyExisting() {
        //inMemoryStorage.addUser(userInStorage);
        assertThrows(UserAlreadyRegisteredException.class, () -> inMemoryStorage.addUser(userInStorage),
            "Method should throw UserAlreadyRegisteredException when trying to register user with already existing username");
    }

    @Test
    void testContainsUserFalse() {
        assertFalse(inMemoryStorage.containsUser(userUnexisting),
            "Method should return false if the user is not in storage");
    }

    @Test
    void testContainsUserTrue() {
        //inMemoryStorage.addUser(userInStorage);
        assertTrue(inMemoryStorage.containsUser(userInStorage), "Method should return true if the user is in storage");
    }

    @Test
    void testAddMoneyToWalletOk() {
        double moneyToAdd = 50.00;
        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);
        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);
        assertEquals(inMemoryStorage.getWalletBalance(userInStorage), moneyToAdd * 2, 0.01,
            "Method should add the money to the user's balance");
    }

    @Test
    void testAddMoneyToWalletInvalidMoney() {
        double moneyToAdd = -15.32;
        assertThrows(IllegalArgumentException.class, () -> inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd),
            "Method should throw IllegalArgumentException if the money argument is less than 0");
    }

    @Test
    void testAddMoneyToWalletForBalanceChangeWithInvalidMoney() {
        try {
            double moneyToAdd = -15.32;
            inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);
        } catch (IllegalArgumentException e) {
            assertEquals(inMemoryStorage.getWalletBalance(userInStorage), 0.00, 0.01,
                "Method should not add the money to the user's balance");
        }
    }

    @Test
    void testBuyCryptocurrencyForAddingNewCryptocurrencyToWalletOk() throws NotEnoughMoneyException {
        double moneyToAdd = 50.00;
        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);

        double moneyToSpend = 30.00;
        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpend);

        var userInvestments = inMemoryStorage.getInvestments(userInStorage);
        List<StockInfo> expected = List.of(new StockInfo(moneyToSpend / cryptocurrency.priceUsd(),
            cryptocurrency.priceUsd()));

        assertIterableEquals(expected, userInvestments.get(cryptocurrency.assetId()),
            "To user's wallet should be added the right quantity and cryptocurrency buying price");
    }

    @Test
    void testBuyCryptocurrencyForAddingExistingCryptocurrencyToWalletOk() throws NotEnoughMoneyException {
        double moneyToAdd = 1000.00;
        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);

        double moneyToSpendOnFirstBuy = 200.00;
        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnFirstBuy);

        double moneyToSpendOnSecondBuy = 500.00;
        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnSecondBuy);

        var userInvestments = inMemoryStorage.getInvestments(userInStorage);
        List<StockInfo> expected = List.of(
            new StockInfo(moneyToSpendOnFirstBuy / cryptocurrency.priceUsd(), cryptocurrency.priceUsd()),
            new StockInfo(moneyToSpendOnSecondBuy / cryptocurrency.priceUsd(), cryptocurrency.priceUsd())
        );

        List<StockInfo> actual = userInvestments.get(cryptocurrency.assetId());
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected),
            "To user's wallet should be added the right quantity and cryptocurrency buying price");
    }

    @Test
    void testBuyCryptocurrencyForRemovingSpentMoneyFromBalance() throws NotEnoughMoneyException {
        double moneyToAdd = 50.00;
        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);

        double moneyToSpend = 30.00;
        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpend);

        assertEquals(inMemoryStorage.getWalletBalance(userInStorage), moneyToAdd - moneyToSpend, 0.01,
            "The money spent must be deducted from user's balance");
    }

    @Test
    void testBuyCryptocurrencyWithMoneyMoreThanBalance() {
        double moneyToSpend = 30.00;
        assertThrows(NotEnoughMoneyException.class,
            () -> inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpend),
            "Method should throw NotEnoughMoneyException when money are greater than the available balance.");
    }

    @Test
    void testBuyCryptocurrencyWithNegativeMoney() {
        double moneyToSpend = -15;
        assertThrows(IllegalArgumentException.class,
            () -> inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpend),
            "Method should throw IllegalArgumentException when money is less than or equals to 0.");
    }

    @Test
    void testSellCryptocurrencyNotInWallet() {
        assertThrows(CryptocurrencyNotFoundException.class,
            () -> inMemoryStorage.sellCryptocurrencyFromWallet(userInStorage, cryptocurrency),
            "Method should throw CryptocurrencyNotFoundException when invoked with currency unavailable in user's wallet");
    }

    @Test
    void testSellCryptocurrencyOk() throws NotEnoughMoneyException, CryptocurrencyNotFoundException {
        double moneyToAdd = 1000.00;
//        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);
//
//        double moneyToSpendOnFirstBuy = 200.00;
//        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnFirstBuy);
//
//        double moneyToSpendOnSecondBuy = 500.00;
//        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnSecondBuy);

        buyCryptocurrency(moneyToAdd, inMemoryStorage, userInStorage);

        inMemoryStorage.sellCryptocurrencyFromWallet(userInStorage, cryptocurrency);

        var userInvestments = inMemoryStorage.getInvestments(userInStorage);

        assertFalse(userInvestments.containsKey(cryptocurrency.assetId()),
            "The cryptocurrency should be removed from user's wallet");
    }

    @Test
    void testSellCryptocurrencyForBalanceChangeOk() throws NotEnoughMoneyException, CryptocurrencyNotFoundException {
        double moneyToAdd = 1000.00;
//        inMemoryStorage.addMoneyToWallet(userInStorage, moneyToAdd);
//
//        double moneyToSpendOnFirstBuy = 200.00;
//        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnFirstBuy);
//
//        double moneyToSpendOnSecondBuy = 500.00;
//        inMemoryStorage.buyCryptocurrency(userInStorage, cryptocurrency, moneyToSpendOnSecondBuy);

        buyCryptocurrency(moneyToAdd, inMemoryStorage, userInStorage);
        inMemoryStorage.sellCryptocurrencyFromWallet(userInStorage, cryptocurrency);

        assertEquals(moneyToAdd, inMemoryStorage.getWalletBalance(userInStorage), 0.01,
            "Method should add the profit to user's balance.");
    }

    private void buyCryptocurrency(double balance, InMemoryStorage inMemoryStorage, User user) throws NotEnoughMoneyException {
        inMemoryStorage.addMoneyToWallet(user, balance);

        double moneyToSpendOnFirstBuy = 200.00;
        inMemoryStorage.buyCryptocurrency(user, cryptocurrency, moneyToSpendOnFirstBuy);

        double moneyToSpendOnSecondBuy = 500.00;
        inMemoryStorage.buyCryptocurrency(user, cryptocurrency, moneyToSpendOnSecondBuy);
    }

    @Test
    void testGetInvestments() {

    }

    @Test
    void testUploadFromDB() {
        try (Reader reader = new StringReader("""
            [
                [
                    {
                        "username": "Pesho",
                        "password": "\u00025����wę�\u003cY��h�\u0019�B6�5\u0014��4O��83"
                    },
                    {
                        "balance": 0.0,
                        "cryptocurrencies": {}
                    }
                ]
            ]
            """)) {
            inMemoryStorage.uploadFromDB(reader);
            assertEquals(1, inMemoryStorage.getSize(), "Method should load the information from the reader.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testUploadToDB() {
        try (Writer writer = new StringWriter()) {
            inMemoryStorage.uploadToDB(writer);
            assertFalse(writer.toString().isBlank(), "Method should write the users in storage to the writer.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
