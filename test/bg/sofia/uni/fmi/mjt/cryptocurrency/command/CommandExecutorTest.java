package bg.sofia.uni.fmi.mjt.cryptocurrency.command;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {
    private static Cryptocurrency cryptocurrency;
    private SocketAddress socketAddress;
    private InMemoryStorage storage;
    @Mock
    private CryptocurrencyStorage cryptocurrencyStorage;
    @Mock
    private Logger logger;
    private CommandExecutor commandExecutor;

    @BeforeAll
    static void setUp() {
        cryptocurrency = new Cryptocurrency("BTC", "Bitcoin", 1, 22711,
            102894431436.49, 2086392323256.16, 57929168359984.54,
            9166.207274778093436220194944);
    }

    @BeforeEach
    void setUpCase() {
        cryptocurrencyStorage = mock(CryptocurrencyStorage.class);
        logger = mock(Logger.class);

        socketAddress = new InetSocketAddress(3456);
        storage = new InMemoryStorage();
        commandExecutor = new CommandExecutor(storage, cryptocurrencyStorage, logger);
    }

    @Test
    void testRegisterUserOk() {
        commandExecutor.register(socketAddress, "whale", "blind");
        assertEquals(1, commandExecutor.getActiveUserCount(),
            "User should be made active after registration");
    }

    @Test
    void testRegisterUserWithInvalidUsername() {
        commandExecutor.register(socketAddress, " ", "blind");
        assertEquals(0, commandExecutor.getActiveUserCount(),
            "User should not be made active during registration if his username is invalid.");
    }

    @Test
    void testRegisterUserWithInvalidPassword() {
        commandExecutor.register(socketAddress, "whale", "  ");
        assertEquals(0, commandExecutor.getActiveUserCount(),
            "User should not be made active during registration if his password is invalid.");
    }

    @Test
    void testRegisterUserThatExists() {
        commandExecutor.register(socketAddress, "whale", "blind");
        commandExecutor.register(socketAddress, "whale", "blind");

        assertEquals(1, commandExecutor.getActiveUserCount(),
            "User should not be made active during registration if his credentials exist.");
    }

    @Test
    void testLoginUserOk() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        storage.addUser(new User("whale", "blind"));
        commandExecutor.login(socketAddress, "whale", "blind");
        assertEquals(1, commandExecutor.getActiveUserCount(),
            "User should be made active after login");
    }

    @Test
    void testLoginUserWithInvalidUsername() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        storage.addUser(new User("whale", "blind"));
        commandExecutor.login(socketAddress, "wheal", "blind");
        assertEquals(0, commandExecutor.getActiveUserCount(),
            "User should not be made active during login if his username is invalid.");
    }

    @Test
    void testLoginUserWithInvalidPassword() {
        commandExecutor.login(socketAddress, "whale", "blond");
        assertEquals(0, commandExecutor.getActiveUserCount(),
            "User should not be made active during login if his password is invalid.");
    }

    @Test
    void testDepositWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.deposit(socketAddress, 10);
        assertEquals(10, storage.getWalletBalance(user),
            "Money should be added to wallet if user is logged in and money is positive.");
    }

    @Test
    void testDepositWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.deposit(socketAddress, 10);
        assertEquals(0, storage.getWalletBalance(user),
            "Money should not be added to wallet if user is logged out.");

    }

    @Test
    void testBuyWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException,
        CryptocurrencyNotFoundException {
        when(cryptocurrencyStorage.getCryptocurrency("BTC")).thenReturn(cryptocurrency);
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.deposit(socketAddress, 2000);

        commandExecutor.buy(socketAddress, "BTC", 1000);
        assertEquals(1000, storage.getWalletBalance(user),
            "Cryptocurrency should be added to wallet if user is logged in and money is positive.");
    }

    @Test
    void testBuyWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);

        commandExecutor.buy(socketAddress, "BTC", 1000);
        assertEquals(0, storage.getWalletBalance(user),
            "Cryptocurrency should not be added to wallet if user is logged out.");
    }

    @Test
    void testSellWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException,
        CryptocurrencyNotFoundException {
        when(cryptocurrencyStorage.getCryptocurrency("BTC")).thenReturn(cryptocurrency);
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.deposit(socketAddress, 2000);

        commandExecutor.buy(socketAddress, "BTC", 1000);
        commandExecutor.sell(socketAddress, "BTC");

        assertEquals(2000, storage.getWalletBalance(user),
            "Cryptocurrency should be removed from wallet if user is logged in and money is positive.");
    }

    @Test
    void testSellWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);

        commandExecutor.sell(socketAddress, "BTC");
        assertEquals(0, storage.getWalletBalance(user),
            "Cryptocurrency should not be removed from wallet if user is logged out.");
    }

    @Test
    void testDisconnectingFromServerWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.disconnect(socketAddress);

        assertEquals(0, commandExecutor.getActiveUserCount(),
            "Logged in user should be removed from the active users when disconnecting from server.");
    }

    @Test
    void testGetWalletSummaryWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException,
        CryptocurrencyNotFoundException {
        when(cryptocurrencyStorage.getCryptocurrency("BTC")).thenReturn(cryptocurrency);
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.deposit(socketAddress, 2000);

        commandExecutor.buy(socketAddress, "BTC", 1000);

        assertTrue(commandExecutor.getWalletSummary(socketAddress).contains("BTC"),
            "Method should return information of user's balance and profit for each bought cryptocurrency" +
                " if user is logged in.");
    }

    @Test
    void testGetWalletSummaryWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);

        assertFalse(commandExecutor.getWalletSummary(socketAddress).contains("Balance"),
            "Method should not return information of user's balance and and profit for each bought " +
                "cryptocurrency if user is not logged in.");
    }

    @Test
    void testGetWalletOverallSummaryWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException,
        CryptocurrencyNotFoundException {
        when(cryptocurrencyStorage.getCryptocurrency("BTC")).thenReturn(cryptocurrency);
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");
        commandExecutor.deposit(socketAddress, 2000);

        commandExecutor.buy(socketAddress, "BTC", 1000);

        assertTrue(commandExecutor.getOverallWalletSummary(socketAddress).contains("Overall"),
            "Method should return information about the user's overall profit of his investments if user is logged in.");
    }

    @Test
    void testGetWalletOverallSummaryWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        User user = new User("whale", "blind");
        storage.addUser(user);

        assertFalse(commandExecutor.getOverallWalletSummary(socketAddress).contains("Overall"),
            "Method should not return information about the user's overall profit of his investments " +
                "if user is  not logged in.");

    }

    @Test
    void testListCryptocurrencyWithLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        when(cryptocurrencyStorage.getAllCryptocurrencies()).thenReturn(List.of(cryptocurrency));
        User user = new User("whale", "blind");
        storage.addUser(user);
        commandExecutor.login(socketAddress, "whale", "blind");

        assertTrue(commandExecutor.listCryptocurrencies(socketAddress).contains("BTC"),
            "Method should return information of all available to buy cryptocurrencies if user is logged in.");
    }

    @Test
    void testListCryptocurrencyWithNotLoggedUser() throws NoSuchAlgorithmException, UserAlreadyRegisteredException {
        when(cryptocurrencyStorage.getAllCryptocurrencies()).thenReturn(List.of(cryptocurrency));
        User user = new User("whale", "blind");
        storage.addUser(user);

        assertFalse(commandExecutor.listCryptocurrencies(socketAddress).contains("BTC"),
            "Method should not return information of all available to buy cryptocurrencies if user is not logged in.");
    }
}
