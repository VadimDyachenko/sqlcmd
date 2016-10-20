package sqlcmd.controller;

import sqlcmd.controller.command.*;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

import static sqlcmd.controller.AvailableCommand.*;

final class CommandExecutor {

    private final Map<AvailableCommand, Command> commandMap = new HashMap<>();

    CommandExecutor(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        commandMap.put(DB_CONNECT, new DBConnect(connectionStatusHelper, manager, view));
        commandMap.put(DB_LIST_AVAILABLE_TABLE, new DBListTableNames(connectionStatusHelper, manager, view));
        commandMap.put(DB_SELECT_TABLE, new DBSelectTable(connectionStatusHelper, manager, view));
        commandMap.put(TABLE_PRINT, new TablePrintData(connectionStatusHelper, manager, view));
        commandMap.put(TABLE_CREATE_RECORD, new TableCreateRecord(connectionStatusHelper, manager, view));
        commandMap.put(TABLE_UPDATE_RECORD, new TableUpdateRecord(manager, view));
        commandMap.put(TABLE_CLEAR, new TableClear(connectionStatusHelper, manager, view));
        commandMap.put(RETURN, new Return(connectionStatusHelper));
        commandMap.put(EXIT, new Exit(manager, view));
        commandMap.put(PRINT_CURRENT_CONNECTION_STATUS, new ConnectionStatus(connectionStatusHelper, manager, view));
    }

    public void execute(AvailableCommand command) throws InterruptOperationException {
        commandMap.get(command).execute();
    }
}
