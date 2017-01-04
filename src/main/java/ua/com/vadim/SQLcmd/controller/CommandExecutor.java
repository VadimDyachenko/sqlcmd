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

    CommandExecutor(RunParameters runParameters, DatabaseManager manager, View view) {
        commands.put(DB_CONNECT, new DBConnect(runParameters, manager, view));
        commands.put(DB_AVAILABLE_TABLE, new DBListTableNames(manager, view));
        commands.put(DB_SELECT_TABLE, new DBSelectTable(runParameters, manager, view));
        commands.put(TABLE_PRINT, new TablePrintData(runParameters, manager, view));
        commands.put(TABLE_CREATE_RECORD, new TableCreateRecord(runParameters, manager, view));
        commands.put(TABLE_UPDATE_RECORD, new TableUpdateRecord(runParameters, manager, view));
        commands.put(TABLE_CLEAR, new TableClear(runParameters, manager, view));
        commands.put(RETURN, new Return(runParameters));
        commands.put(EXIT, new Exit(manager, view));
        commands.put(PRINT_CONNECTION_STATUS, new ConnectionStatus(runParameters, manager, view));
    }

    public void execute(AvailableCommand command) throws ExitException {
        commands.get(command).execute();
    }
}
