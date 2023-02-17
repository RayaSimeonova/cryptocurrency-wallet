package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;
import java.util.Objects;

public class RegisterCommand extends AbstractCommand {
    private final String username;
    private final String password;

    public RegisterCommand(String username, String password, CommandExecutor commandExecutor) {
        super(CommandType.REGISTER, commandExecutor);

        this.username = username;
        this.password = password;
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.register(userAddress, username, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterCommand that = (RegisterCommand) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
