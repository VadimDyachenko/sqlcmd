package sqlcmd;

import sqlcmd.command.CommandExecutor;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.ConsoleHelper;
import sqlcmd.command.Operation;
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
        view.writeMessage("Welcome to SQLCmd!\n");

        try {
//            commandExecutor.execute(Operation.LOGIN);
            Operation operation;
            do {
                operation = commandExecutor.askMainOperation();
                commandExecutor.execute(operation);
            }
            while (true); //operation != Operation.EXIT);

        } catch (InterruptOperationException e) {
            view.printExitMessage();
        }
        System.exit(0);
    }
}
