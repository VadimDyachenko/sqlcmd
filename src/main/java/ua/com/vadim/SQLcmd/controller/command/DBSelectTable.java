package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBSelectTable implements Command {
    private RunParameters runParameters;
    private DatabaseManager manager;
    private View view;

    public DBSelectTable(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"DBConnect to database\" first.\n");
            return;
        }

        Set<String> tableNames = getAvailableTableNames();

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n",
                    runParameters.getDatabaseName()));
            return;
        }

        view.writeMessage("Enter table name. Available tables:");

        printAvailableTables(tableNames);

        do {
            String tableName = view.readLine();
            if (tableNames.contains(tableName)) {
                runParameters.setTableName(tableName);
                runParameters.setTableLevel(true);
                break;
            } else {
                view.writeMessage("Enter correct table name. Available tables:");
                printAvailableTables(tableNames);
            }
        }  while (true);
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
