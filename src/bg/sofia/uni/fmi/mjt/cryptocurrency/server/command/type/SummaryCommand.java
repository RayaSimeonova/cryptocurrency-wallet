package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;

public class SummaryCommand extends AbstractCommand {

    public SummaryCommand(CommandExecutor commandExecutor) {
        super(CommandType.SUMMARY, commandExecutor);
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.getWalletSummary(userAddress);
    }
}
