package sqlcmd.controller.command;

import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBSelectTable implements Command {
    private DatabaseManager manager;
    private View view;
    private ConnectionStatusHelper connectionStatusHelper;

    public DBSelectTable(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"DBConnect to database\" first.\n");
            return;
        }

        Set<String> tableNames = getAvailableTableNames();

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n", connectionStatusHelper.getCurrentDatabaseName()));
            return;
        }

        view.writeMessage("Enter table name. Available tables:");

        printAvailableTables(tableNames);

        while (true) {
            String tableName = view.readLine();
            if (tableNames.contains(tableName)) {
                connectionStatusHelper.setCurrentTableName(tableName);
                connectionStatusHelper.setTableLevel(true);
                break;
            } else {
                view.writeMessage("Enter correct table name. Available tables:");
                printAvailableTables(tableNames);
            }
        }
    }

    private Set<String> getAvailableTableNames() {
        Set<String> result = new LinkedHashSet<>();
        try {
            result = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }
        return result;
    }

    private void printAvailableTables(Set<String> tableNames) {

        view.writeMessage(tableNames.toString() + "\n");
    }

}
