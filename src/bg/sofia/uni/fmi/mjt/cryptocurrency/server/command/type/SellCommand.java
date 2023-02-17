package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;
import java.util.Objects;

public class SellCommand extends AbstractCommand {
    private final String cryptocurrencyCode;

    public SellCommand(String cryptocurrencyCode, CommandExecutor commandExecutor) {
        super(CommandType.SELL, commandExecutor);

        this.cryptocurrencyCode = cryptocurrencyCode;
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.sell(userAddress, cryptocurrencyCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SellCommand that = (SellCommand) o;
        return Objects.equals(cryptocurrencyCode, that.cryptocurrencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cryptocurrencyCode);
    }
}
