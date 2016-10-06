package sqlcmd.commandsystem.commands;

import sqlcmd.commandsystem.Command;
import sqlcmd.commandsystem.CommandExecutor;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

public class Connect implements Command {
    private DatabaseManager manager;
    private View view;

    public Connect(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        view.writeMessage("Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n");

        while (true) {
            String databaseName = getInputString("Please, enter database name:");
            String login = getInputString("Enter you login:");
            String password = getInputString("Enter you password:");

            try {
                if (manager.isConnected()) {
                    manager.disconnect();
                }
                manager.connect(databaseName, login, password);
                manager.setCurrentDatabaseName(databaseName);
                view.writeMessage("Connection successful!\n");
                break;

            } catch (Exception e) {
                view.writeMessage("Connection failed: " + e.getMessage());
                view.writeMessage("Try again.");
            }
        }

    }

    private String getInputString(String message) throws InterruptOperationException {

        view.writeMessage(message);
        return view.readLine();

    }
}
