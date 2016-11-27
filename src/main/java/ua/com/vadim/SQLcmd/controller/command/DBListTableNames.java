package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBListTableNames implements Command {
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public DBListTableNames(RunParameters runParameters, DatabaseManager manager, View view) {
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

        Set<String> tableNames = new LinkedHashSet<>();
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        if (tableNames.isEmpty()) {
            view.writeMessage(String.format("There are no tables in the database <%s>\n", runParameters.getDatabaseName()));
            return;
        }

        printResult(tableNames);
    }

    private void printResult(Set<String> tableNames) {
        view.writeMessage("Available tables:");
        view.writeMessage(tableNames.toString());
    }
}