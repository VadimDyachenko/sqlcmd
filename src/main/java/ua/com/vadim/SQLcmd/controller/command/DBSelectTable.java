package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBSelectTable implements Command {
    private final ResourceBundle res;
    private final RunParameters runParameters;
    private final DatabaseManager manager;
    private final View view;

    public DBSelectTable(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "DBSelectTable", new UTF8Control());
    }

    @Override
    public void execute() {
        if (manager.isConnected()) {
            selectTable();
        } else {
            view.writeMessage(res.getString("dbselect.no.connection"));
        }
    }

    private void selectTable() {
        Set<String> tableNames = getAvailableTableNames();
        if (tableNames.isEmpty()) {
            view.writeMessage(res.getString("dbselect.no.tables"));
        } else {
            setTable(tableNames);
        }
    }

    private void setTable(Set<String> tableNames) {
        view.writeMessage(res.getString("dbselect.enter.name.tables"));
        printAvailableTables(tableNames);
        do {
            String tableName = view.readLine();
            if (tableNames.contains(tableName)) {
                runParameters.setTableName(tableName);
                runParameters.setTableLevel(true);
                break;
            } else {
                view.writeMessage(res.getString("dbselect.enter.correct.name.tables"));
                printAvailableTables(tableNames);
            }
        } while (true);
    }

    private Set<String> getAvailableTableNames() {
        Set<String> result = new LinkedHashSet<>();
        try {
            result = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage(res.getString("dbselect.failure") + e.getMessage());
        }
        return result;
    }

    private void printAvailableTables(Set<String> tableNames) {
        view.writeMessage(tableNames.toString());
    }
}