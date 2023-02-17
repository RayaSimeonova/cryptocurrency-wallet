package bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private double balance;
    private final Map<String, List<StockInfo>> cryptocurrencies;

    public Wallet() {
        cryptocurrencies = new HashMap<>();
    }

    public void addMoney(double money) {
        balance += money;
    }

    public double getBalance() {
        return balance;
    }

    public void removeCryptocurrency(String cryptoId, double sellingPrice) {
        List<StockInfo> stockInfos = cryptocurrencies.remove(cryptoId);

        for (StockInfo stockInfo : stockInfos) {
            addMoney(stockInfo.quantity() * sellingPrice);
        }
    }

    public void addCryptocurrency(String cryptoId, double buyingPrice, double money) {
        double boughtQuantity;
        if (buyingPrice > 0) {
            balance -= money;
            boughtQuantity = money / buyingPrice;
        } else {
            boughtQuantity = money;
        }
        StockInfo stockInfo = new StockInfo(boughtQuantity, buyingPrice);

        cryptocurrencies.putIfAbsent(cryptoId, new ArrayList<>());
        cryptocurrencies.get(cryptoId).add(stockInfo);
    }

    public Map<String, List<StockInfo>> getCryptocurrencies() {
        return cryptocurrencies;
    }

    public boolean containsCryptocurrency(String cryptoId) {
        return cryptocurrencies.containsKey(cryptoId);
    }
}
