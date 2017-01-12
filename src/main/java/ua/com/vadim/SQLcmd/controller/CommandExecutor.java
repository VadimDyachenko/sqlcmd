package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.controller.command.*;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.util.HashMap;
import java.util.Map;

import static ua.com.vadim.SQLcmd.controller.AvailableCommand.*;

class CommandExecutor {

    private Map<AvailableCommand, Command> commands = new HashMap<>();

    CommandExecutor(RunParameters parameters, DatabaseManager manager, View view) {
        commands.put(DB_CONNECT, new DBConnect(parameters, manager, view));
        commands.put(DB_AVAILABLE_TABLE, new DBListTableNames(parameters, manager, view));
        commands.put(DB_SELECT_TABLE, new DBSelectTable(parameters, manager, view));
        commands.put(TABLE_PRINT, new TablePrintData(parameters, manager, view));
        commands.put(TABLE_CREATE_RECORD, new TableCreateRecord(parameters, manager, view));
        commands.put(TABLE_UPDATE_RECORD, new TableUpdateRecord(parameters, manager, view));
        commands.put(TABLE_CLEAR, new TableClear(parameters, manager, view));
        commands.put(RETURN, new Return(parameters, manager, view));
        commands.put(EXIT, new Exit(parameters, manager, view));
        commands.put(PRINT_CONNECTION_STATUS, new ConnectionStatus(parameters, manager, view));
    }

    public void execute(AvailableCommand command) throws ExitException {
        commands.get(command).execute();
    }
}
