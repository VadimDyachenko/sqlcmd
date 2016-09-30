package sqlcmd;

import sqlcmd.command.CommandExecutor;
import sqlcmd.database.DatabaseManager;
import sqlcmd.database.JDBCDatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.ConsoleHelper;
import sqlcmd.view.Operation;
import sqlcmd.view.View;

public class Controller {

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }

    private void run() {
        DatabaseManager manager = new JDBCDatabaseManager();
        View view = new ConsoleHelper();
        CommandExecutor commandExecutor = new CommandExecutor(manager, view);

        try {
            commandExecutor.execute(Operation.LOGIN);
            Operation operation;
            do {
                operation = view.askOperation();
                commandExecutor.execute(operation);
            }
            while (operation != Operation.EXIT);

        } catch (InterruptOperationException e) {
            view.printExitMessage();
        }
        System.exit(0);
    }
}
