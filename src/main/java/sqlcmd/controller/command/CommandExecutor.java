package sqlcmd.controller.command;

import sqlcmd.controller.AvailableOperation;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<AvailableOperation, Command> commandMap = new HashMap<>();

    public CommandExecutor(DatabaseManager manager, View view) {
        commandMap.put(AvailableOperation.CONNECT, new Connect(manager, view));
        commandMap.put(AvailableOperation.LIST_TABLE, new ListTableNames(manager, view));
        commandMap.put(AvailableOperation.SELECT_TABLE, new SelectTable(manager, view));
        commandMap.put(AvailableOperation.TABLE_PRINT, new TablePrintData(manager, view));
        commandMap.put(AvailableOperation.TABLE_CREATE_RECORD, new TableCreateRecord(manager, view));
        commandMap.put(AvailableOperation.TABLE_UPDATE_RECORD, new TableUpdateRecord(manager, view));
        commandMap.put(AvailableOperation.TABLE_CLEAR, new TableClear(manager, view));
        commandMap.put(AvailableOperation.RETURN, new Rerturn(manager, view));
        commandMap.put(AvailableOperation.EXIT, new Exit(manager, view));
    }

    public void execute(AvailableOperation availableOperation) throws InterruptOperationException {
        commandMap.get(availableOperation).execute();
    }
}
