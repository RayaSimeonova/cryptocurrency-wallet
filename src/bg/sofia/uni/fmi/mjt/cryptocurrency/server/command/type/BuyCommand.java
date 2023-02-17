package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;
import java.util.Objects;

public class BuyCommand extends AbstractCommand {
    private final String cryptocurrencyCode;
    private final double money;

    public BuyCommand(String cryptocurrencyCode, double money, CommandExecutor commandExecutor) {
        super(CommandType.BUY, commandExecutor);

        this.cryptocurrencyCode = cryptocurrencyCode;
        this.money = money;
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.buy(userAddress, cryptocurrencyCode, money);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuyCommand that = (BuyCommand) o;
        return Double.compare(that.money, money) == 0 &&
            Objects.equals(cryptocurrencyCode, that.cryptocurrencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cryptocurrencyCode, money);
    }
}
