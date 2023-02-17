package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;

public class OverallSummaryCommand extends AbstractCommand {

    public OverallSummaryCommand(CommandExecutor commandExecutor) {
        super(CommandType.OVERALL_SUMMARY, commandExecutor);
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.getOverallWalletSummary(userAddress);
    }
}
