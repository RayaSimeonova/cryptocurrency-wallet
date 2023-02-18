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

    /***
     * Saves the user in the database.
     * Any from the database stored in memory should be automatically updated.
     *
     * @throws UserAlreadyRegisteredException when there is a user registered with this username
     */
    void addUser(User user) throws UserAlreadyRegisteredException;

    /***
     *
     * @return true if the user is registered and false otherwise
     */
    boolean containsUser(User user);

    /***
     * Adds the money to the user's wallet.
     * @param money the money to be added to the user's wallet
     *
     * @throws IllegalArgumentException if money is not positive
     */
    void addMoneyToWallet(User user, double money);

    /***
     *
     * @return the money the user has in his wallet
     */
    double getWalletBalance(User user);

    /***
     * Removes all the cryptocurrency's quantity from the user's investments
     *
     * @param cryptocurrency the cryptocurrency to sell from the user's investments.
     * @throws CryptocurrencyNotFoundException if the user does not have the cryptocurrency in stock
     */
    void sellCryptocurrencyFromWallet(User user, Cryptocurrency cryptocurrency) throws CryptocurrencyNotFoundException;

    /***
     *
     * @return map of the user's investments
     */
    Map<String, List<StockInfo>> getInvestments(User user);

    /***
     * Adds the cryptocurrency to the user's investments with quantity equals to ( money / cryptocurrency price )
     * If the cryptocurrency price is 0, then the quantity added equals the money wished to spend on the cryptocurrency
     *
     * @param cryptocurrency which to add to the user's investments
     * @param money which to spend on the cryptocurrency
     * @throws NotEnoughMoneyException if the user's balance is less than the money
     */
    void buyCryptocurrency(User user, Cryptocurrency cryptocurrency, double money) throws NotEnoughMoneyException;

    /***
     * Imports data from the database.
     * @param reader the reader from which data is imported
     */
    void uploadFromDB(Reader reader);

    /***
     * Saves changed data to the database.
     * @param writer the writer on which to save data
     */
    void uploadToDB(Writer writer);
}
