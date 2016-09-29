package sqlcmd.command;

import sqlcmd.view.ConsoleHelper;
import sqlcmd.database.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;

public class LoginCommand implements Command {
    private DatabaseManager manager;
    private ConsoleHelper consoleHelper;

    public LoginCommand(DatabaseManager manager, ConsoleHelper consoleHelper) {
        this.manager = manager;
        this.consoleHelper = consoleHelper;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String databaseName;
        String login;
        String password;
        consoleHelper.writeMessage("Welcome to SQLCmd!\n" +
                "Enter database name, login and password.\n");
        while (true) {
            consoleHelper.writeMessage("Please, enter database name:");
            databaseName = consoleHelper.readLine();
            consoleHelper.writeMessage("Enter you login:");
            login = consoleHelper.readLine();
            consoleHelper.writeMessage("Enter you password:");
            password = consoleHelper.readLine();
            try {
                manager.connect(databaseName, login, password);
                consoleHelper.writeMessage("Connection successful!\n");
                break;
            } catch (Exception e) {
                consoleHelper.writeMessage("Connection failed: " + e.getMessage());
                consoleHelper.writeMessage("Try again.");
            }
        }

    }
}
