package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.StockInfo;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Wallet;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.NotEnoughMoneyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryStorage {
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    private Map<User, Wallet> userWallet;

    public InMemoryStorage() {
        userWallet = new HashMap<>();
    }

    public void addUser(User user) throws UserAlreadyRegisteredException {
        boolean usernameExists = userWallet.keySet().stream().anyMatch(user1 -> user1.hasSameUsername(user));
        if (usernameExists) {
            throw new UserAlreadyRegisteredException("User with this username is already registered.");
        }
        userWallet.put(user, new Wallet());
    }

    public void uploadFromDB(Reader reader) {
        //TODO
//        try {
        Type type = new TypeToken<Map<User, Wallet>>() {
        }.getType();
        //Reader reader = new FileReader(filename);
        userWallet = GSON.fromJson(reader, type);
//        } catch (FileNotFoundException e) {
//            return;
//        }
    }

    public void uploadToDB(Writer writer) {
        //TODO
        //try (Writer writer = new FileWriter(filename)) {
        GSON.toJson(userWallet, writer);
        //} catch (IOException e) {
            //log
            return;
        //}
    }

    public boolean containsUser(User user) {
        return userWallet.containsKey(user);
    }

    public void addMoneyToWallet(User user, Double money) {
        validateMoneyIsPositive(money);
        userWallet.get(user).addMoney(money);
    }

    public Double getWalletBalance(User user) {
        return userWallet.get(user).getBalance();
    }

    public void sellCryptocurrencyFromWallet(User user, Cryptocurrency cryptocurrency)
        throws CryptocurrencyNotFoundException {
        if (!userWallet.get(user).containsCryptocurrency(cryptocurrency.assetId())) {
            throw new CryptocurrencyNotFoundException(String.format("Cryptocurrency %s is not in wallet",
                cryptocurrency.assetId()));
        }
        userWallet.get(user).removeCryptocurrency(cryptocurrency.assetId(), cryptocurrency.priceUsd());
    }

    public Map<String, List<StockInfo>> getInvestments(User user) {
        return userWallet.get(user).getCryptocurrencies();
    }

    public void buyCryptocurrency(User user, Cryptocurrency cryptocurrency, double money)
        throws NotEnoughMoneyException {
        validateMoneyIsPositive(money);
        if (money > userWallet.get(user).getBalance()) {
            throw new NotEnoughMoneyException("The amount available must be greater or equal to the money.");
        }
        userWallet.get(user).addCryptocurrency(cryptocurrency.assetId(), cryptocurrency.priceUsd(), money);
    }

    public int getSize() {
        return userWallet.size();
    }

    private void validateMoneyIsPositive(double money) {
        if (money <= 0) {
            throw new IllegalArgumentException("Money to be added to user wallet must be a positive integer.");
        }
    }
}
