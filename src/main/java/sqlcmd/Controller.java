package sqlcmd;

import sqlcmd.command.CommandExecutor;
import sqlcmd.database.DatabaseManager;
import sqlcmd.database.JDBCDatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.ConsoleHelper;
import sqlcmd.view.Operation;

public class Controller {

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }

    private void run() {
        DatabaseManager manager = new JDBCDatabaseManager();
        ConsoleHelper consoleHelper = new ConsoleHelper();
        CommandExecutor commandExecutor = new CommandExecutor(manager, consoleHelper);

        try {
            commandExecutor.execute(Operation.LOGIN);
            Operation operation;
            do {
                operation = consoleHelper.askOperation();
                commandExecutor.execute(operation);
            }
            while (operation != Operation.EXIT);

        } catch (InterruptOperationException e) {
            consoleHelper.printExitMessage();
        }
        System.exit(0);
    }
}
