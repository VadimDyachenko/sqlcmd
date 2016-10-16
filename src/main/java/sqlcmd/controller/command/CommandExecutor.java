package sqlcmd.controller.command;

import sqlcmd.controller.AvailableOperation;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<AvailableOperation, Command> commandMap = new HashMap<>();

    public CommandExecutor(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        commandMap.put(AvailableOperation.CONNECT, new Connect(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.LIST_TABLE, new ListTableNames(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.SELECT_TABLE, new SelectTable(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.TABLE_PRINT, new TablePrintData(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.TABLE_CREATE_RECORD, new TableCreateRecord(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.TABLE_UPDATE_RECORD, new TableUpdateRecord(manager, view));
        commandMap.put(AvailableOperation.TABLE_CLEAR, new TableClear(connectionStatusHelper, manager, view));
        commandMap.put(AvailableOperation.RETURN, new Return(connectionStatusHelper));
        commandMap.put(AvailableOperation.EXIT, new Exit(manager, view));
        commandMap.put(AvailableOperation.CONNECTION_STATUS, new ConnectionStatus(connectionStatusHelper, manager, view));

    }

    public void execute(AvailableOperation availableOperation) throws InterruptOperationException {
        commandMap.get(availableOperation).execute();
    }
}
