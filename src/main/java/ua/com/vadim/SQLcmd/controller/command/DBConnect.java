package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnect extends AbstractCommand {

    private final ResourceBundle resource;

    public DBConnect(RunParameters parameters, DatabaseManager manager, View view) {
        super(parameters, manager, view);
        resource = ResourceBundle.getBundle("DBConnect", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        if (manager.isConnected()) {
            connectWithNewParameters();
        } else {
            connectDefaultParameters();
        }
    }

    private void connectDefaultParameters() throws ExitException {
        try {
            manager.connect(
                    parameters.getDatabaseName(),
                    parameters.getUserName(),
                    parameters.getPassword());
        } catch (SQLException e) {
            view.writeMessage(resource.getString("dbconnect.failed") + " " + e.getMessage());
            view.writeMessage(resource.getString("dbconnect.default.failed"));
            throw new ExitException();
        }
        view.writeMessage(resource.getString("dbconnect.successful"));
    }

    private void connectWithNewParameters() throws ExitException {

        view.writeMessage(resource.getString("dbconnect.enter.connection.parameters"));

        do try {
            manager.disconnect();

            view.writeMessage(resource.getString("dbconnect.enter.database.name"));
            String databaseName = readLine();

            view.writeMessage(resource.getString("dbconnect.enter.login"));
            String login = readLine();

            view.writeMessage(resource.getString("dbconnect.enter.password"));
            String password = readLine();

            manager.connect(databaseName, login, password);
            parameters.setDatabaseName(databaseName);

        } catch (SQLException e) {
            view.writeMessage(resource.getString("dbconnect.failed") + " " + e.getMessage());
            view.writeMessage(resource.getString("dbconnect.try.again"));
        }
        while (!manager.isConnected());
    }
}
