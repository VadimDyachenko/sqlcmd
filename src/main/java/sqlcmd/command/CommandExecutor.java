package sqlcmd.command;

import sqlcmd.model.DatabaseManager;
import sqlcmd.view.Operation;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<Operation, Command> commandMap = new HashMap<>();

    public CommandExecutor(DatabaseManager manager, View view) {
        commandMap.put(Operation.LOGIN, new LoginCommand(manager, view));
        commandMap.put(Operation.LIST_TABLE, new ListTableNamesCommand(manager, view));
        commandMap.put(Operation.TABLE_PRINT, new PrintTableDataCommand(manager, view));
//        commandMap.put(Operation.TABLE_CHANGE, new LoginCommand(manager, consoleHelper));
        commandMap.put(Operation.EXIT, new ExitCommand(manager, view));
    }

    public void execute(Operation operation) throws InterruptOperationException {
        commandMap.get(operation).execute();
    }
}
