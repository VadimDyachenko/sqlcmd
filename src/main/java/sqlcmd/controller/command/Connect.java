package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.sql.SQLException;

public class Connect implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;

    public Connect(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
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
                controller.setCurrentDatabaseName(databaseName);
                view.writeMessage("Connection successful!\n");
                break;
            } catch (SQLException e) {
                view.writeMessage("Connection failed: " + e.getMessage());
                view.writeMessage("Try again.");
            }
        } while (!manager.isConnected());
    }

    private String getInputString(String message) throws InterruptOperationException {
        view.writeMessage(message);
        return view.readLine();
    }
}
