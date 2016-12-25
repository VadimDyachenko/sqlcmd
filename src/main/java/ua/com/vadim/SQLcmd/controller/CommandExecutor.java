package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.controller.command.*;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.util.HashMap;
import java.util.Map;

final class CommandExecutor {

    private final Map<AvailableCommand, Command> commandMap = new HashMap<>();

    CommandExecutor(RunParameters runParameters, DatabaseManager manager, View view) {
        commandMap.put(AvailableCommand.DB_CONNECT, new DBConnect(runParameters, manager, view));
        commandMap.put(AvailableCommand.DB_LIST_AVAILABLE_TABLE, new DBListTableNames(runParameters, manager, view));
        commandMap.put(AvailableCommand.DB_SELECT_TABLE, new DBSelectTable(runParameters, manager, view));
        commandMap.put(AvailableCommand.TABLE_PRINT, new TablePrintData(runParameters, manager, view));
        commandMap.put(AvailableCommand.TABLE_CREATE_RECORD, new TableCreateRecord(runParameters, manager, view));
        commandMap.put(AvailableCommand.TABLE_UPDATE_RECORD, new TableUpdateRecord(runParameters, manager, view));
        commandMap.put(AvailableCommand.TABLE_CLEAR, new TableClear(runParameters, manager, view));
        commandMap.put(AvailableCommand.RETURN, new Return(runParameters));
        commandMap.put(AvailableCommand.EXIT, new Exit(runParameters, manager, view));
        commandMap.put(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS, new ConnectionStatus(runParameters, manager, view));
    }

    public void execute(AvailableCommand command) throws ExitException {
        commandMap.get(command).execute();
    }
}
