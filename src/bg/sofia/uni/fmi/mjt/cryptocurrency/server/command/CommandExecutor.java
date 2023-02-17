package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.StockInfo;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.NotEnoughMoneyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserNotLoggedException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users.InMemoryStorage;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.cryptocurrency.CryptocurrencyStorage;

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
    private final InMemoryStorage storage;
    private final CryptocurrencyStorage cryptocurrencyStorage;
    private final Logger logger;

    public CommandExecutor(InMemoryStorage inMemoryStorage, CryptocurrencyStorage cryptocurrencyStorage,
                           Logger logger) {
        activeUsers = new HashMap<>();
        storage = inMemoryStorage;
        this.cryptocurrencyStorage = cryptocurrencyStorage;
        this.logger = logger;
    }

    public String register(SocketAddress userAddress, String username, String password) {
        try {
            User user = new User(username, password);
            storage.addUser(user);
            activeUsers.put(userAddress, user);

            return String.format("Registration successful! %s", OUTPUT_END);
        } catch (UserAlreadyRegisteredException e) {
            //log
            return e.getMessage() + OUTPUT_END;
        } catch (NoSuchAlgorithmException e) {
            return logNoSuchAlgorithmException(e, username);
        } catch (IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String login(SocketAddress userAddress, String username, String password) {
        try {
            User user = new User(username, password);
            if (storage.containsUser(user)) {
                activeUsers.put(userAddress, user);
                return String.format("Login successful! %s", OUTPUT_END);
            }
        } catch (NoSuchAlgorithmException e) {
            return logNoSuchAlgorithmException(e, username);
        } catch (IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
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
            storage.addMoneyToWallet(user, money);

            return String.format("Successful deposit of %,.2f USD. Current balance is %,.2f USD. %s",
                money, storage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        } catch (IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String buy(SocketAddress userAddress, String cryptoId, double money) {
        try {
            User user = getUser(userAddress);
            Cryptocurrency cryptocurrency = cryptocurrencyStorage.getCryptocurrency(cryptoId);
            storage.buyCryptocurrency(user, cryptocurrency, money);
            return String.format("Successful purchase of %s for %,.2f USD. Current balance is %,.2f USD %s",
                cryptoId, money, storage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException e) {
            //log?
            return e.getMessage();
        } catch (IllegalArgumentException e) {
            return e.getMessage() + OUTPUT_END;
        } catch (NotEnoughMoneyException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyNotFoundException e) {
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String sell(SocketAddress userAddress, String cryptoId) {
        try {
            User user = getUser(userAddress);
            Cryptocurrency cryptocurrency = cryptocurrencyStorage.getCryptocurrency(cryptoId);
            storage.sellCryptocurrencyFromWallet(user, cryptocurrency);
            return String.format("%s is sold. Current balance is %,.2f %s", cryptoId,
                storage.getWalletBalance(user), OUTPUT_END);
        } catch (UserNotLoggedException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyNotFoundException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String getWalletSummary(SocketAddress userAddress) {
        try {
            User user = getUser(userAddress);
            Map<String, List<StockInfo>> investments = storage.getInvestments(user);
            StringBuilder walletSummary =
                new StringBuilder("Balance: " + storage.getWalletBalance(user) + System.lineSeparator());

            for (var investment : investments.entrySet()) {
                double investmentProfit = getInvestmentProfit(investment);
                walletSummary.append(String.format("%s: %,.2f USD", investment.getKey(), investmentProfit))
                    .append(System.lineSeparator());
            }

            return walletSummary + OUTPUT_END;
        } catch (UserNotLoggedException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyNotFoundException e) {
            //log
            return e.getMessage() + OUTPUT_END;
        }
    }

    public String getOverallWalletSummary(SocketAddress userAddress) {
        try {
            User user = getUser(userAddress);

            Map<String, List<StockInfo>> investments = storage.getInvestments(user);
            double overallProfit = 0.0;

            for (var investment : investments.entrySet()) {
                overallProfit += getInvestmentProfit(investment);
            }
            return String.format("Overall investment profit: %,.2f USD %s", overallProfit, OUTPUT_END);
        } catch (UserNotLoggedException e) {
            //log?
            return e.getMessage() + OUTPUT_END;
        } catch (CryptocurrencyNotFoundException e) {
            //log
            return e.getMessage() + OUTPUT_END;
        }
    }

    private double getInvestmentProfit(Map.Entry<String, List<StockInfo>> investment)
        throws CryptocurrencyNotFoundException {
        double sellingPrice = cryptocurrencyStorage.getCryptocurrency(investment.getKey()).priceUsd();
        double profit = 0;
        for (StockInfo stockInfo : investment.getValue()) {
            profit += stockInfo.getQuantity() * (sellingPrice - stockInfo.getBuyingPrice());
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
            //log?
            return e.getMessage() + OUTPUT_END;
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
}
