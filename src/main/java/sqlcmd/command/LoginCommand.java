package sqlcmd.command;

import sqlcmd.database.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

public class LoginCommand implements Command {
    private DatabaseManager manager;
    private View view;

    public LoginCommand(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String databaseName;
        String login;
        String password;
        view.writeMessage("Welcome to SQLCmd!\n" +
                "Enter database name, login and password.\n");
        while (true) {
            view.writeMessage("Please, enter database name:");
            databaseName = view.readLine();
            view.writeMessage("Enter you login:");
            login = view.readLine();
            view.writeMessage("Enter you password:");
            password = view.readLine();
            try {
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
