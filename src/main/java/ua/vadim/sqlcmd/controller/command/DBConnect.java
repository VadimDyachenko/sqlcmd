package ua.vadim.sqlcmd.controller.command;

import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;
import ua.vadim.sqlcmd.model.DatabaseManager;
import ua.vadim.sqlcmd.exception.InterruptOperationException;
import ua.vadim.sqlcmd.view.View;

import java.sql.SQLException;

public class DBConnect implements Command {
    private DatabaseManager manager;
    private View view;
    private ConnectionStatusHelper connectionStatusHelper;

    public DBConnect(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        view.writeMessage("Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n");

        do {
            String databaseName = getInputString("Please, enter database name:");
            String login = getInputString("Enter you login:");
            String password = getInputString("Enter you password:");

            try {
                if (manager.isConnected()) {
                    manager.disconnect();
                }
                manager.connect(databaseName, login, password);
                connectionStatusHelper.setCurrentDatabaseName(databaseName);
                view.writeMessage("Connection successful!\n");
                break;
            } catch (SQLException e) {
                view.writeMessage("Connection failed: " + e.getMessage());
                view.writeMessage("Try again or type <exit>.");
            }
        } while (!manager.isConnected());
    }

    private String getInputString(String message) throws InterruptOperationException {
        view.writeMessage(message);
        return view.readLine();
    }
}
