package sqlcmd.command;

import sqlcmd.view.ConsoleHelper;
import sqlcmd.database.DatabaseManager;
import sqlcmd.view.Operation;
import sqlcmd.exception.InterruptOperationException;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {
    private final Map<Operation, Command> commandMap = new HashMap<>();

    public CommandExecutor(DatabaseManager manager, ConsoleHelper consoleHelper) {
        commandMap.put(Operation.LOGIN, new LoginCommand(manager, consoleHelper));
        commandMap.put(Operation.EXIT, new ExitCommand(manager, consoleHelper));
    }

    public void execute(Operation operation) throws InterruptOperationException {
        commandMap.get(operation).execute();
    }
}
