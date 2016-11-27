package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.BreakException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnect implements Command {
    private final ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public DBConnect(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "dbconnect");
    }

    @Override
    public void execute() throws BreakException {
        try {
            if (manager.isConnected()) {
                connectWithNewParameters();
            } else {
                connectDefaultParameters();
            }
        } catch (SQLException e) {
            view.writeMessage(res.getString("dbconnect.failed") + " " + e.getMessage());
            connectWithNewParameters();
        }
    }

    private void connectDefaultParameters() throws SQLException {
        manager.connect(
                runParameters.getDatabaseName(),
                runParameters.getUserName(),
                runParameters.getPassword());
    }

    private void connectWithNewParameters() {
        view.writeMessage(res.getString("dbconnect.enter.connection.parameters"));
        try {
            manager.disconnect();
            do {
                String databaseName = getInputString(res.getString("dbconnect.enter.database.name"));
                String login = getInputString(res.getString("dbconnect.enter.login"));
                String password = getInputString(res.getString("dbconnect.enter.password"));
                manager.connect(databaseName, login, password);
                runParameters.setDatabaseName(databaseName);
            } while (!manager.isConnected());
        } catch (SQLException e) {
            view.writeMessage(res.getString("dbconnect.failed") + " " + e.getMessage());
        }
    }

    private String getInputString(String message) {
        view.writeMessage(message);
        return view.readLine();
    }
}
