package sqlcmd.controller.command;

import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBListTableNames implements Command {
    private DatabaseManager manager;
    private View view;
    private ConnectionStatusHelper connectionStatusHelper;

    public DBListTableNames(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"DBConnect to database\" first.\n");
            return;
        }

        Set<String> tableNames = new LinkedHashSet<>();
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n", connectionStatusHelper.getCurrentDatabaseName()));
            return;
        }

        printResult(tableNames);
    }

    private void printResult(Set<String> tableNames) {
        view.writeMessage("Available tables:");
        view.writeMessage(tableNames.toString() + "\n");
    }
}