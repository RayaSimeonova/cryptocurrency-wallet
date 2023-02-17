package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;

public class ListCommand extends AbstractCommand {

    public ListCommand(CommandExecutor commandExecutor) {
        super(CommandType.LIST, commandExecutor);
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.listCryptocurrencies(userAddress);
    }
}
