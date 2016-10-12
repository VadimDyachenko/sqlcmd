package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vadim on 02.10.16.
 */
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

        List<String> tableNames = getAvailableTableNames();

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

    private List<String> getAvailableTableNames() {
        List<String> result = new ArrayList<>();
        try {
            result = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }
        return result;
    }

    private void printAvailableTables(List<String> tableNames) {
        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";
        view.writeMessage(availableTables + "\n");
    }

}
