package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;

import java.util.Objects;

public abstract class AbstractCommand implements Command {
    private final CommandType commandType;
    protected CommandExecutor commandExecutor;

    public AbstractCommand(CommandType commandType, CommandExecutor commandExecutor) {
        this.commandType = commandType;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCommand that = (AbstractCommand) o;
        return commandType == that.commandType && Objects.equals(commandExecutor, that.commandExecutor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandType, commandExecutor);
    }
}
