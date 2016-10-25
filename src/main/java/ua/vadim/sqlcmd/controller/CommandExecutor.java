package ua.vadim.sqlcmd.controller;

import ua.vadim.sqlcmd.controller.command.*;
import ua.vadim.sqlcmd.model.DatabaseManager;
import ua.vadim.sqlcmd.exception.InterruptOperationException;
import ua.vadim.sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

final class CommandExecutor {

    private final Map<AvailableCommand, Command> commandMap = new HashMap<>();

    CommandExecutor(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        commandMap.put(AvailableCommand.DB_CONNECT, new DBConnect(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.DB_LIST_AVAILABLE_TABLE, new DBListTableNames(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.DB_SELECT_TABLE, new DBSelectTable(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.TABLE_PRINT, new TablePrintData(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.TABLE_CREATE_RECORD, new TableCreateRecord(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.TABLE_UPDATE_RECORD, new TableUpdateRecord(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.TABLE_CLEAR, new TableClear(connectionStatusHelper, manager, view));
        commandMap.put(AvailableCommand.RETURN, new Return(connectionStatusHelper));
        commandMap.put(AvailableCommand.EXIT, new Exit(manager, view));
        commandMap.put(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS, new ConnectionStatus(connectionStatusHelper, manager, view));
    }

    public void execute(AvailableCommand command) throws InterruptOperationException {
        commandMap.get(command).execute();
    }
}
