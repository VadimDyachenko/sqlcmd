package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class SelectTable implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;

    public SelectTable(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"Connect to database\" first.\n");
            return;
        }

        Set<String> tableNames = getAvailableTableNames();

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n", controller.getCurrentDatabaseName()));
            return;
        }

        view.writeMessage("Enter table name. Available tables:");

        printAvailableTables(tableNames);

        while (true) {
            String tableName = view.readLine();
            if (tableNames.contains(tableName)) {
                controller.setCurrentTableName(tableName);
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
