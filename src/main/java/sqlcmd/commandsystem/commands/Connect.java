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
        String databaseName;
        String login;
        String password;
        view.writeMessage("Enter database name, login and password.\n" +
                          "Type 'exit' for exit program.\n" );
        while (true) {
            view.writeMessage("Please, enter database name:");
            databaseName = view.readLine();
            manager.setCurrentDatabaseName(databaseName);
            view.writeMessage("Enter you login:");
            login = view.readLine();
            view.writeMessage("Enter you password:");
            password = view.readLine();
            try {
                if (manager.isConnected()) {
                    manager.disconnect();
                }
                manager.connect(databaseName, login, password);
                view.writeMessage("Connection successful!\n");
                break;
            } catch (Exception e) {
                view.writeMessage("Connection failed: " + e.getMessage());
                view.writeMessage("Try again.");
            }
        }

    }
}
