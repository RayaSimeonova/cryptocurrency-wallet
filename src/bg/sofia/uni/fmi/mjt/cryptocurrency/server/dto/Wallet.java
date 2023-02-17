package bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private double balance;
    private Map<String, List<StockInfo>> cryptocurrencies;

    public Wallet() {
        cryptocurrencies = new HashMap<>();
    }

    public void addMoney(Double money) {
        balance += money;
    }

    public double getBalance() {
        return balance;
    }

    public void removeCryptocurrency(String cryptoId, double sellingPrice) {
        List<StockInfo> stockInfos = cryptocurrencies.remove(cryptoId);

        for (StockInfo stockInfo : stockInfos) {
            addMoney(stockInfo.getQuantity() * sellingPrice);
        }
    }

    public void addCryptocurrency(String cryptoId, double buyingPrice, double money) {
        balance -= money;
        double boughtQuantity = money / buyingPrice;
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
