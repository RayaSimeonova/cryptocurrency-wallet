package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.StockInfo;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyUnavailable;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.NotEnoughMoneyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserNotLoggedException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryUserStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.UserStorage;

import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandExecutor {
    private static final String OUTPUT_END = System.lineSeparator() + "--finished--" + System.lineSeparator();
    private final Map<SocketAddress, User> activeUsers;
    private final UserStorage userStorage;
    private final CryptocurrencyStorage cryptocurrencyStorage;
//    private final CryptocurrencyClient cryptocurrencyClient;
//    private final HttpClient httpClient;

    private final Logger logger;

    public CommandExecutor(UserStorage inMemoryUserStorage, CryptocurrencyStorage cryptocurrencyStorage,
                           Logger logger) {
        activeUsers = new HashMap<>();
        userStorage = inMemoryUserStorage;
        this.cryptocurrencyStorage = cryptocurrencyStorage;
        this.logger = logger;

//        httpClient = HttpClient.newBuilder().build();
//        cryptocurrencyClient = new CryptocurrencyClient(httpClient);
    }

    public String register(SocketAddress userAddress, String username, String password) {
        try {
            if (isUserLogged(userAddress)) {
                return String.format("You are logged! Can't attempt registration! %s", OUTPUT_END);
            }
            User user = new User(username, password);
            userStorage.addUser(user);
            activeUsers.put(userAddress, user);

            return String.format("Registration successful! %s", OUTPUT_END);
        } catch (UserAlreadyRegisteredException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (NoSuchAlgorithmException e) {
            return logNoSuchAlgorithmException(e, username);
        } catch (IllegalArgumentException e) {
            return String.format("Username and password must be non-null, non-empty strings! %s", OUTPUT_END);
        }
    }

    public String login(SocketAddress userAddress, String username, String password) {
        try {
            User user = new User(username, password);
            if (userStorage.containsUser(user)) {
                activeUsers.put(userAddress, user);
                return String.format("Login successful! %s", OUTPUT_END);
            }
        } catch (NoSuchAlgorithmException e) {
            return logNoSuchAlgorithmException(e, username);
        } catch (IllegalArgumentException e) {
            return String.format("Username and password must be non-null, non-empty strings! %s", OUTPUT_END);
        }
        return String.format("Incorrect Credentials! %s", OUTPUT_END);
    }

    private String logNoSuchAlgorithmException(NoSuchAlgorithmException e, String username) {
        String msg = String.format("Operation failed due to security reasons %s", OUTPUT_END);
        logger.log(Level.SEVERE, String.format("For user %s: %s", username, msg), e);
        return msg;
    }

    public String deposit(SocketAddress userAddress, double money) {
        try {
            User user = getUser(userAddress);
            userStorage.addMoneyToWallet(user, money);

            return String.format("Successful deposit of %,.2f USD. Current balance is %,.2f USD. %s",
                money, userStorage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException | IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String buy(SocketAddress userAddress, String cryptoId, double money) {
        try {
            User user = getUser(userAddress);
            Cryptocurrency cryptocurrency = cryptocurrencyStorage.getCryptocurrency(cryptoId);
            userStorage.buyCryptocurrency(user, cryptocurrency, money);
            return String.format("Successful purchase of %s for %,.2f USD. Current balance is %,.2f USD %s",
                cryptoId, money, userStorage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException | CryptocurrencyNotFoundException |
                 IllegalArgumentException | NotEnoughMoneyException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyUnavailable e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return String.format("Cryptocurrency list is unavailable at the moment. Please try again later. %s",
                OUTPUT_END);
        }
    }

    public String sell(SocketAddress userAddress, String cryptoId) {
        try {
            User user = getUser(userAddress);
            Cryptocurrency cryptocurrency = cryptocurrencyStorage.getCryptocurrency(cryptoId);
            userStorage.sellCryptocurrencyFromWallet(user, cryptocurrency);
            return String.format("%s is sold. Current balance is %,.2f %s", cryptoId,
                userStorage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException | CryptocurrencyNotFoundException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyUnavailable e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return String.format("Cryptocurrency list is unavailable at the moment. Please try again later. %s",
                OUTPUT_END);
        }
    }

    public String getWalletSummary(SocketAddress userAddress) {
        try {
            User user = getUser(userAddress);
            Map<String, List<StockInfo>> investments = userStorage.getInvestments(user);
            StringBuilder walletSummary =
                new StringBuilder("Balance: " + userStorage.getWalletBalance(user) + System.lineSeparator());

            for (var investment : investments.entrySet()) {
                double investmentProfit = getInvestmentProfit(investment);
                walletSummary.append(String.format("%s: %,.2f USD", investment.getKey(), investmentProfit))
                    .append(System.lineSeparator());
            }

            return walletSummary + OUTPUT_END;
        } catch (UserNotLoggedException | CryptocurrencyNotFoundException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyUnavailable e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return String.format("Cryptocurrency list is unavailable at the moment to calculate investments profits. " +
                    "Please try again later. %s", OUTPUT_END);
        }
    }

    public String getOverallWalletSummary(SocketAddress userAddress) {
        try {
            User user = getUser(userAddress);

            Map<String, List<StockInfo>> investments = userStorage.getInvestments(user);
            double overallProfit = 0.0;

            for (var investment : investments.entrySet()) {
                overallProfit += getInvestmentProfit(investment);
            }
            return String.format("Overall investment profit: %,.2f USD %s", overallProfit, OUTPUT_END);
        } catch (UserNotLoggedException | CryptocurrencyNotFoundException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyUnavailable e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return String.format("Cryptocurrency list is unavailable at the moment to calculate investments profit. " +
                    "Please try again later. %s", OUTPUT_END);
        }
    }

    private double getInvestmentProfit(Map.Entry<String, List<StockInfo>> investment)
        throws CryptocurrencyNotFoundException, CryptocurrencyUnavailable {
        double sellingPrice = cryptocurrencyStorage.getCryptocurrency(investment.getKey()).priceUsd();
        double profit = 0.0;
        for (StockInfo stockInfo : investment.getValue()) {
            profit += stockInfo.quantity() * (sellingPrice - stockInfo.buyingPrice());
        }
        return profit;
    }

    public String listCryptocurrencies(SocketAddress userAddress) {
        try {
            getUser(userAddress);
            StringBuilder cryptocurrenciesList = new StringBuilder();
            for (Cryptocurrency cryptocurrency : cryptocurrencyStorage.getAllCryptocurrencies()) {
                cryptocurrenciesList.append(cryptocurrency.toString()).append(System.lineSeparator());
            }
            return cryptocurrenciesList + OUTPUT_END;
        } catch (UserNotLoggedException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyUnavailable e) {
            logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return String.format("Cryptocurrency list is unavailable at the moment. Please try again later. %s",
                OUTPUT_END);
        }
    }

    public String disconnect(SocketAddress userAddress) {
        activeUsers.remove(userAddress);
        return String.format("Disconnecting from the server... %s", OUTPUT_END);
    }

    public int getActiveUserCount() {
        return activeUsers.size();
    }

    private User getUser(SocketAddress userAddress) throws UserNotLoggedException {
        User user = activeUsers.get(userAddress);
        if (user == null) {
            throw new UserNotLoggedException("User must be logged in to use the system.");
        }
        return user;
    }

    private boolean isUserLogged(SocketAddress userAddress) {
        if (activeUsers.containsKey(userAddress)) {
            return true;
        }
        return false;
    }
}
