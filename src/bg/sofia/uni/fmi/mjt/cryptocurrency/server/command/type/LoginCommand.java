package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.net.SocketAddress;
import java.util.Objects;

public class LoginCommand extends AbstractCommand {
    private String username;
    private String password;

    public LoginCommand(String username, String password, CommandExecutor commandExecutor) {
        super(CommandType.LOGIN, commandExecutor);

        this.username = username;
        this.password = password;
    }

    @Override
    public String execute(SocketAddress userAddress) {
        return commandExecutor.login(userAddress, username, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginCommand that = (LoginCommand) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
