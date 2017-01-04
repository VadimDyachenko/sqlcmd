package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class DBSelectTable extends AbstractCommand implements Command {
    private final ResourceBundle resource;
    private final RunParameters runParameters;
    private final DatabaseManager manager;
    private final View view;

    public DBSelectTable(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("DBSelectTable", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        if (manager.isConnected()) {
            selectTable();
        } else {
            view.writeMessage(resource.getString("dbselect.no.connection"));
        }
    }

    private void selectTable() throws ExitException {
        Set<String> tableNames = getAvailableTableNames();
        if (tableNames.isEmpty()) {
            view.writeMessage(resource.getString("dbselect.no.tables"));
        } else {
            setTable(tableNames);
        }
    }

    private void setTable(Set<String> tableNames) throws ExitException {
        view.writeMessage(resource.getString("dbselect.enter.name.tables"));
        printAvailableTables(tableNames);
        do {
            String tableName = readLine();
            if (tableNames.contains(tableName)) {
                runParameters.setTableName(tableName);
                runParameters.setTableLevel(true);
                break;
            } else {
                view.writeMessage(resource.getString("dbselect.enter.correct.name.tables"));
                printAvailableTables(tableNames);
            }
        } while (true);
    }

    private Set<String> getAvailableTableNames() {
        Set<String> result = new LinkedHashSet<>();
        try {
            result = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage(resource.getString("dbselect.failure") + e.getMessage());
        }
        return result;
    }

    private void printAvailableTables(Set<String> tableNames) {
        view.writeMessage(tableNames.toString());
    }

    @Override
    View getView() {
        return view;
    }
}