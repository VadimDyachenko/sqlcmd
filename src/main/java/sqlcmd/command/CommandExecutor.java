package sqlcmd.command;

import sqlcmd.Operation;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<Operation, Command> commandMap = new HashMap<>();

    public CommandExecutor(DatabaseManager manager, View view) {
        commandMap.put(Operation.CONNECT, new Connect(manager, view));
        commandMap.put(Operation.LIST_TABLE, new ListTableNames(manager, view));
        commandMap.put(Operation.SELECT_TABLE, new SelectTable(manager, view));
        commandMap.put(Operation.TABLE_PRINT, new TablePrintData(manager, view));
        commandMap.put(Operation.TABLE_CREATE_RECORD, new TableCreateRecord(manager, view));
        commandMap.put(Operation.TABLE_UPDATE_RECORD, new TableUpdateRecord(manager, view));
        commandMap.put(Operation.TABLE_CLEAR, new TableClear(manager, view));
        commandMap.put(Operation.RETURN, new Rerturn(manager, view));
        commandMap.put(Operation.EXIT, new Exit(manager, view));
    }

    public void execute(Operation operation) throws InterruptOperationException {
        commandMap.get(operation).execute();
    }
}
