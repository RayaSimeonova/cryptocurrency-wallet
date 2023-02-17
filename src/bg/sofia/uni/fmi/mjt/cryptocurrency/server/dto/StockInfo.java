package bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto;

import java.util.Objects;

public class StockInfo {
    private Double quantity;
    private final Double buyingPrice;

    public StockInfo(Double quantity, Double buyingPrice) {
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
    }

    public Double getQuantity() {
        return quantity;
    }

    public Double getBuyingPrice() {
        return buyingPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockInfo stockInfo = (StockInfo) o;
        return Objects.equals(quantity, stockInfo.quantity) &&
            Objects.equals(buyingPrice, stockInfo.buyingPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, buyingPrice);
    }
}
