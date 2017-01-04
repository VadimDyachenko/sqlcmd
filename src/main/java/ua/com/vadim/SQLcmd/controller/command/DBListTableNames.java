package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class DBListTableNames implements Command {
    private final ResourceBundle resource;
    private final DatabaseManager manager;
    private final View view;

    public DBListTableNames(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("DBListTableNames", new UTF8Control());
    }

    @Override
    public void execute() {
        if (manager.isConnected()) {
            listTableNames();
        } else {
            view.writeMessage(resource.getString("dblist.no.connection"));
        }
    }

    private void listTableNames() {
        Set<String> tableNames = getTableNames();
        if (tableNames.isEmpty()) {
            view.writeMessage(resource.getString("dblist.no.tables"));
        } else {
            printResult(tableNames);
        }
    }

    private Set<String> getTableNames() {
        Set<String> tableNames = new LinkedHashSet<>();
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage(resource.getString("dblist.failure") + e.getMessage());
        }
        return tableNames;
    }

    private void printResult(Set<String> tableNames) {
        view.writeMessage(resource.getString("dblist.available.tables"));
        view.writeMessage(tableNames.toString());
    }
}