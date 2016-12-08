package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnect implements Command {
    private final ResourceBundle res;
    private final DatabaseManager manager;
    private final View view;
    private final RunParameters runParameters;

    public DBConnect(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "DBConnect", new UTF8Control());
    }

    @Override
    public void execute() {
        if (manager.isConnected()) {
            connectWithNewParameters();
        } else {
            connectDefaultParameters();
        }
    }

    private void connectDefaultParameters() throws ExitException {
        try {
            manager.connect(
                    runParameters.getDatabaseName(),
                    runParameters.getUserName(),
                    runParameters.getPassword());
        } catch (SQLException e) {
            view.writeMessage(res.getString("dbconnect.failed") + " " + e.getMessage());
            view.writeMessage(res.getString("dbconnect.default.failed"));
            throw new ExitException();
        }
        view.writeMessage(res.getString("dbconnect.successful"));
    }

    private void connectWithNewParameters() throws ExitException {
        view.writeMessage(res.getString("dbconnect.enter.connection.parameters"));
        do try {
            manager.disconnect();
            String databaseName = getInputString(res.getString("dbconnect.enter.database.name"));
            String login = getInputString(res.getString("dbconnect.enter.login"));
            String password = getInputString(res.getString("dbconnect.enter.password"));
            manager.connect(databaseName, login, password);
            runParameters.setDatabaseName(databaseName);
        } catch (SQLException e) {
            view.writeMessage(res.getString("dbconnect.failed") + " " + e.getMessage());
            view.writeMessage(res.getString("dbconnect.try.again"));
        }
        while (!manager.isConnected()) ;
    }

    private String getInputString(String message) throws ExitException {
        view.writeMessage(message);
        return view.readLine();
    }
}
