package bg.sofia.uni.fmi.mjt.cryptocurrency.server.command;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.*;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CommandArgumentsException;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private final static int MAX_COMMAND_ARGUMENTS = 2;
    private final static int ONE_COMMAND_ARGUMENT = 1;
    private final static int ZERO_COMMAND_ARGUMENTS = 0;
    private final static String CLIENT_INPUT_DELIMITER = " ";

    public static Command newCommand(String clientInput, CommandExecutor commandExecutor)
        throws CommandArgumentsException {

        String[] parsedClientInput = clientInput.strip()
            .replaceAll("\\s+", CLIENT_INPUT_DELIMITER).split(CLIENT_INPUT_DELIMITER);

        String commandName = parsedClientInput[0];
        List<String> args = Arrays.stream(parsedClientInput).skip(1).toList();

        return switch(CommandType.valueOfCommandName(commandName)) {
            case REGISTER -> createRegisterCommand(args, commandExecutor);
            case LOGIN -> createLoginCommand(args, commandExecutor);
            case DEPOSIT -> createDepositCommand(args, commandExecutor);
            case BUY -> createBuyCommand(args, commandExecutor);
            case SELL -> createSellCommand(args, commandExecutor);
            case LIST -> createListCommand(args, commandExecutor);
            case SUMMARY -> createSummaryCommand(args, commandExecutor);
            case OVERALL_SUMMARY -> createOverallSummaryCommand(args, commandExecutor);
            case DISCONNECT -> createDisconnectCommand(args, commandExecutor);
        };
    }

    private static Command createRegisterCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != MAX_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Register command must have " +
                MAX_COMMAND_ARGUMENTS + " arguments: username and password");
        }
        return new RegisterCommand(args.get(0), args.get(1), commandExecutor);
    }

    private static Command createLoginCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != MAX_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Login command must have " +
                MAX_COMMAND_ARGUMENTS + " arguments: username and password");
        }
        return new LoginCommand(args.get(0), args.get(1), commandExecutor);
    }

    private static Command createDepositCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ONE_COMMAND_ARGUMENT) {
            throw new CommandArgumentsException("Deposit money command must have " +
                ONE_COMMAND_ARGUMENT + " argument: the money to be deposited");
        }
        return new DepositCommand(Double.parseDouble(args.get(0)), commandExecutor);
    }

    private static Command createBuyCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != MAX_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Buy cryptocurrency command must have " +
                MAX_COMMAND_ARGUMENTS + " arguments: the cryptocurrency code and the money to buy it with");
        }
        return new BuyCommand(args.get(0), Double.parseDouble(args.get(1)), commandExecutor);
    }

    private static Command createSellCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ONE_COMMAND_ARGUMENT) {
            throw new CommandArgumentsException("Sell cryptocurrency command must have " +
                ONE_COMMAND_ARGUMENT + " argument: the code of the cryptocurrency you want to sell");
        }
        return new SellCommand(args.get(0), commandExecutor);
    }

    private static Command createListCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ZERO_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("List cryptocurrencies command must have " +
                ZERO_COMMAND_ARGUMENTS + " arguments");
        }
        return new ListCommand(commandExecutor);
    }

    private static Command createSummaryCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ZERO_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Get summary wallet command must have " +
                ZERO_COMMAND_ARGUMENTS + " arguments");
        }
        return new SummaryCommand(commandExecutor);
    }

    private static Command createOverallSummaryCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ZERO_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Get overall summary wallet command must have " +
                ZERO_COMMAND_ARGUMENTS + " arguments");
        }
        return new OverallSummaryCommand(commandExecutor);
    }

    private static Command createDisconnectCommand(List<String> args, CommandExecutor commandExecutor)
        throws CommandArgumentsException {
        if (args.size() != ZERO_COMMAND_ARGUMENTS) {
            throw new CommandArgumentsException("Disconnect command must have " +
                ZERO_COMMAND_ARGUMENTS + " arguments");
        }
        return new DisconnectCommand(commandExecutor);
    }

}
