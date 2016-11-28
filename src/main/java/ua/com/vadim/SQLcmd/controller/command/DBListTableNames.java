package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class DBListTableNames implements Command {
    private ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public DBListTableNames(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "DBListTableNames", new UTF8Control());
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage(res.getString("dblist.no.connection"));
            return;
        }

        Set<String> tableNames = new LinkedHashSet<>();
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            view.writeMessage(res.getString("dblist.failure") + e.getMessage());
        }

        if (tableNames.isEmpty()) {
            view.writeMessage(res.getString("dblist.no.tables"));
            return;
        }
        printResult(tableNames);
    }

    private void printResult(Set<String> tableNames) {
        view.writeMessage(res.getString("dblist.available.tables"));
        view.writeMessage(tableNames.toString() + "\n");
    }
}