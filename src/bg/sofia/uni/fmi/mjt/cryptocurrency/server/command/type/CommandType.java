package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type;

public enum CommandType {
    REGISTER("register"),
    LOGIN("login"),
    DEPOSIT("deposit-money"),
    BUY("buy"),
    SELL("sell"),
    SUMMARY("get-wallet-summary"),
    OVERALL_SUMMARY("get-wallet-overall-summary"),
    LIST("list-cryptocurrencies"),
    DISCONNECT("disconnect");

    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    public static CommandType valueOfCommandName(String commandName) {
        for (CommandType commandType: CommandType.values()) {
            if (commandType.name.equalsIgnoreCase(commandName)) {
                return commandType;
            }
        }
        throw new IllegalArgumentException("Command type with such name does not exist");
    }
}
