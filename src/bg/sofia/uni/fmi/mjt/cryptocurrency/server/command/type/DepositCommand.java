package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;
import java.util.Objects;

public class DepositCommand extends AbstractCommand {
    private double balance;

    public DepositCommand(double balance, CommandExecutor commandExecutor) {
        super(CommandType.DEPOSIT, commandExecutor);
        this.balance = balance;
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.deposit(userAddress, balance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositCommand that = (DepositCommand) o;
        return Double.compare(that.balance, balance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(balance);
    }
}
