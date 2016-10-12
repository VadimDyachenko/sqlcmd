package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListTableNames implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;

    public ListTableNames(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"Connect to database\" first.\n");
            return;
        }

        List<String> tableNames = new ArrayList<>();
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n", controller.getCurrentDatabaseName()));
            return;
        }

        printResult(tableNames);
    }

    private void printResult(List<String> tableNames) {
        view.writeMessage("Available tables:");
        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]\n";
        view.writeMessage(availableTables);
    }
}