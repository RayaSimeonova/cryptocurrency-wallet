package bg.sofia.uni.fmi.mjt.cryptocurrency.command;

import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.BuyCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.Command;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.DepositCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.DisconnectCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.ListCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.LoginCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.OverallSummaryCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.RegisterCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.SellCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.command.type.SummaryCommand;
import bg.sofia.uni.fmi.mjt.cryptocurrency.server.exception.command.CommandArgumentsException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandCreatorTest {

    @Mock
    private static CommandExecutor commandExecutor;

    @Test
    void testRegisterCommandOk() throws CommandArgumentsException {
        String input = "register john islands";
        Command expected = new RegisterCommand("john", "islands", commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create register command");
    }

    @Test
    void testRegisterCommandWithInvalidNumberOfArguments() {
        String input = "register john";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when register command arguments are less than 2.");
    }

    @Test
    void testRegisterCommandWithTrailingWhitespaces() throws CommandArgumentsException {
        String input = "  register  john  islands   ";
        Command expected = new RegisterCommand("john", "islands", commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create register command");
    }

    @Test
    void testLoginCommandOk() throws CommandArgumentsException {
        String input = "login john islands";
        Command expected = new LoginCommand("john", "islands", commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create login command");
    }

    @Test
    void testLoginCommandWithInvalidNumberOfArguments() {
        String input = "login john";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when login command arguments are less than 2.");
    }

    @Test
    void testLoginCommandWithTrailingWhitespaces() throws CommandArgumentsException {
        String input = "  login  john  islands   ";
        Command expected = new LoginCommand("john", "islands", commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create login command");
    }

    @Test
    void testDepositCommandOk() throws CommandArgumentsException {
        String input = "deposit-money 10";
        Command expected = new DepositCommand(10, commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create deposit command");
    }

    @Test
    void testDepositCommandWithLessArguments() {
        String input = "deposit-money";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when deposit command arguments are less than 1.");
    }

    @Test
    void testDepositCommandWithMoreArguments() {
        String input = "deposit-money 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when deposit command arguments are more than 1.");
    }

    @Test
    void testBuyCommandOk() throws CommandArgumentsException {
        String input = "buy BTC 1000";
        Command expected = new BuyCommand("BTC", 1000, commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create buy command");
    }

    @Test
    void testBuyCommandWithLessArguments() {
        String input = "buy BTC";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when buy command arguments are less than 2.");
    }

    @Test
    void testBuyCommandWithMoreArguments() {
        String input = "buy BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when buy command arguments are more than 2.");
    }

    @Test
    void testSellCommandOk() throws CommandArgumentsException {
        String input = "sell BTC";
        Command expected = new SellCommand("BTC", commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create sell command");
    }

    @Test
    void testSellCommandWithLessArguments() {
        String input = "buy";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when sell command arguments are less than 1.");
    }

    @Test
    void testSellCommandWithMoreArguments() {
        String input = "buy BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when sell command arguments are more than 1.");
    }

    @Test
    void testListCommandOk() throws CommandArgumentsException {
        String input = "list-cryptocurrencies";
        Command expected = new ListCommand(commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create list command");
    }

    @Test
    void testListCommandWithMoreArguments() {
        String input = "list-cryptocurrencies BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when list command has arguments.");
    }

    @Test
    void testSummaryCommandOk() throws CommandArgumentsException {
        String input = "get-wallet-summary";
        Command expected = new SummaryCommand(commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create get-wallet-summary command");
    }

    @Test
    void testSummaryCommandWithMoreArguments() {
        String input = "get-wallet-summary BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when get-wallet-summary command has arguments.");
    }

    @Test
    void testOverallSummaryCommandOk() throws CommandArgumentsException {
        String input = "get-wallet-overall-summary";
        Command expected = new OverallSummaryCommand(commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create get-wallet-overall-summary command");
    }

    @Test
    void testOverallSummaryCommandWithMoreArguments() {
        String input = "get-wallet-summary BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when get-wallet-overall-summary command has arguments.");
    }

    @Test
    void testDisconnectCommandOk() throws CommandArgumentsException {
        String input = "disconnect";
        Command expected = new DisconnectCommand(commandExecutor);
        Command actual = CommandCreator.newCommand(input, commandExecutor);

        assertEquals(expected, actual, "Command creator should accurately create disconnect command");
    }

    @Test
    void testDisconnectCommandWithMoreArguments() {
        String input = "disconnect BTC 10 5";

        assertThrows(CommandArgumentsException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when disconnect command has arguments.");
    }

    @Test
    void testWithUnknownCommand() {
        String input = "unknown BTC 10 5";

        assertThrows(IllegalArgumentException.class, () -> CommandCreator.newCommand(input, commandExecutor),
            "CommandCreator should throw exception when invoked with unknown command.");
    }
}
