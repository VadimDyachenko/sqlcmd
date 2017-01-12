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

    private Command DBConnect;
    private Command DBListTableNames;
    private Command DBSelectTable;
    private Command TablePrintData;
    private Command TableCreateRecord;
    private Command TableUpdateRecord;
    private Command TableClear;
    private Command Return;
    private Command Exit;
    private Command ConnectionStatus;

    CommandExecutor(RunParameters parameters, DatabaseManager manager, View view) {

        createCommands(parameters, manager, view);

        commands.put(DB_CONNECT, DBConnect);
        commands.put(DB_AVAILABLE_TABLE, DBListTableNames);
        commands.put(DB_SELECT_TABLE, DBSelectTable);
        commands.put(TABLE_PRINT, TablePrintData);
        commands.put(TABLE_CREATE_RECORD, TableCreateRecord);
        commands.put(TABLE_UPDATE_RECORD, TableUpdateRecord);
        commands.put(TABLE_CLEAR, TableClear);
        commands.put(RETURN, Return);
        commands.put(EXIT, Exit);
        commands.put(PRINT_CONNECTION_STATUS, ConnectionStatus);
    }

    public void execute(AvailableCommand command) throws ExitException {
        commands.get(command).execute();
    }

    private void createCommands(RunParameters parameters, DatabaseManager manager, View view) {

        DBConnect = new DBConnect(parameters, manager, view);
        DBListTableNames = new DBListTableNames(parameters, manager, view);
        DBSelectTable = new DBSelectTable(parameters, manager, view);
        TablePrintData = new TablePrintData(parameters, manager, view);
        TableCreateRecord = new TableCreateRecord(parameters, manager, view);
        TableUpdateRecord = new TableUpdateRecord(parameters, manager, view);
        TableClear = new TableClear(parameters, manager, view);
        Return = new Return(parameters, manager, view);
        Exit = new Exit(parameters, manager, view);
        ConnectionStatus = new ConnectionStatus(parameters, manager, view);
    }
}
