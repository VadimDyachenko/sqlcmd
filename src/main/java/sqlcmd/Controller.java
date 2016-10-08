package sqlcmd;

import sqlcmd.command.CommandExecutor;
import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.ConsoleHelper;
import sqlcmd.view.View;

public class Controller {
    private DatabaseManager manager;
    private View view;

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }

    private void run() {
        manager = new JDBCDatabaseManager();
        view = new ConsoleHelper();
        CommandExecutor commandExecutor = new CommandExecutor(manager, view);
        view.writeMessage("Welcome to SQLCmd!\n");
        try {
            Operation operation;
            do {
                operation = askOperation();
                commandExecutor.execute(operation);
            }
            while (true);
        } catch (ExitException e1) {
            //NOP
        } catch (InterruptOperationException e2) {
            view.writeMessage("Terminated. Thank you for using SQLCmd. Good luck.");
            //System.exit(0);  <--- закомментировано из-за тестов.
        }
    }

    private Operation askOperation() throws InterruptOperationException {

        printCurrentConnectionAndTable();

        view.writeMessage("Please choose an operation desired or type 'EXIT' for exiting");

        while (true) {
            if (!manager.isTableLayer()) {
                printMainMenu();
            } else {
                printTableMenu();
            }

            String choice = view.readLine();

            try {
                Integer numOfChoice = Integer.parseInt(choice);
                if (manager.isTableLayer()) {
                    return Operation.getTableOperation(numOfChoice);
                } else {
                    return Operation.getMainOperation(numOfChoice);
                }
            } catch (IllegalArgumentException e) {
                view.writeMessage("\nPlease choise correct number:");
            }
        }
    }

    private void printCurrentConnectionAndTable() {
        if (manager.isConnected() && !manager.isTableLayer()) {
            view.writeMessage(String.format("Connected to database: <%s>", manager.getCurrentDatabaseName()));
        } else if (manager.isConnected() && manager.isTableLayer()) {
            view.writeMessage(String.format("Connected to database: <%s>. Selected table: <%s>",
                    manager.getCurrentDatabaseName(), manager.getCurrentTableName()));
        }
    }

    private void printMainMenu() {
        view.writeMessage(
                        "1 - Connect to database\n" +
                        "2 - List all table names\n" +
                        "3 - Select table to work\n" +
                        "4 - Exit"
        );
    }

    private void printTableMenu() {
        view.writeMessage(
                        "1 - Print table data\n" +
                        "2 - Create table record\n" +
                        "3 - Update table record\n" +
                        "4 - Clear table\n" +
                        "5 - Return to previous menu"
        );
    }
}