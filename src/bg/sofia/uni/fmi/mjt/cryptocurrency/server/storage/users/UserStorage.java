package bg.sofia.uni.fmi.mjt.cryptocurrency.server.storage.users;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.Cryptocurrency;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.StockInfo;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto.User;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CryptocurrencyNotFoundException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.NotEnoughMoneyException;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.UserAlreadyRegisteredException;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    void uploadFromDB(Reader reader);
    void uploadToDB(Writer writer);

    void addUser(User user) throws UserAlreadyRegisteredException;
    boolean containsUser(User user);
    void addMoneyToWallet(User user, double money);
    double getWalletBalance(User user);
    void sellCryptocurrencyFromWallet(User user, Cryptocurrency cryptocurrency) throws CryptocurrencyNotFoundException;
    Map<String, List<StockInfo>> getInvestments(User user);
    void buyCryptocurrency(User user, Cryptocurrency cryptocurrency, double money) throws NotEnoughMoneyException;
}
