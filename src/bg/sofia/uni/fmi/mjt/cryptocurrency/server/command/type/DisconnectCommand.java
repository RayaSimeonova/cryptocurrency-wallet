package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;

public class DisconnectCommand extends AbstractCommand {

    public DisconnectCommand(CommandExecutor commandExecutor) {
        super(CommandType.DISCONNECT, commandExecutor);
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.disconnect(userAddress);
    }
}
