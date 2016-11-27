package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;

public class DBConnect implements Command {
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public DBConnect(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        try {
            if (!manager.isConnected()) {
                manager.connect(
                        runParameters.getDatabaseName(),
                        runParameters.getUserName(),
                        runParameters.getPassword());
                view.writeMessage("Connection successful!\n");
            } else {
                connectToAnotherDatabase();
            }
        } catch (SQLException e) {
            view.writeMessage("Connection failed: " + e.getMessage());
            view.writeMessage("Try again or type <exit>.");
            connectToAnotherDatabase();
        }
    }

    private void connectToAnotherDatabase() {
        view.writeMessage("Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n");

        do {
            String databaseName = getInputString("Enter database name:");
            String login = getInputString("Enter you login:");
            String password = getInputString("Enter you password:");
            try {
                manager.disconnect();
                manager.connect(databaseName, login, password);
                runParameters.setDatabaseName(databaseName);
                view.writeMessage("Connection successful!\n");
                break;
            } catch (SQLException e) {
                view.writeMessage("Connection failed: " + e.getMessage());
                view.writeMessage("Try again or type <break> for return to previous menu.");
            }
        } while (!manager.isConnected());
    }

    private String getInputString(String message) {
        view.writeMessage(message);
        return view.readLine();
    }
}
