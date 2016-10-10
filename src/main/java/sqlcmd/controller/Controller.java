package sqlcmd.controller;

import sqlcmd.controller.command.CommandExecutor;
import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

public class Controller {

    private DatabaseManager manager;
    private View view;

    public Controller(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    public void run() {
        CommandExecutor commandExecutor = new CommandExecutor(manager, view);
        view.writeMessage("Welcome to SQLCmd!\n");
        try {
            AvailableOperation availableOperation;
            do {
                availableOperation = askOperation();
                commandExecutor.execute(availableOperation);
            }
            while (true);
        } catch (ExitException e1) {
            //NOP
        } catch (InterruptOperationException e2) {
            view.writeMessage("Terminated. Thank you for using SQLCmd. Good luck.");
            //System.exit(0);  <--- закомментировано из-за тестов.
        }
    }

    private AvailableOperation askOperation() throws InterruptOperationException {

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
                    return AvailableOperation.getTableOperation(numOfChoice);
                } else {
                    return AvailableOperation.getMainOperation(numOfChoice);
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