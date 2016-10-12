package sqlcmd.controller;

import sqlcmd.controller.command.CommandExecutor;
import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

public class Controller {

    private DatabaseManager manager;
    private View view;
    private CommandExecutor commandExecutor;
    private String currentDatabaseName;
    private String currentTableName;
    private boolean tableLayer = false;

    public Controller(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        commandExecutor = new CommandExecutor(this, manager, view);
    }

    public void run() {
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
            if (!tableLayer) {
                printMainMenu();
            } else {
                printTableMenu();
            }

            String choice = view.readLine();

            try {
                Integer numOfChoice = Integer.parseInt(choice);
                if (tableLayer) {
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
        if (manager.isConnected() && !tableLayer) {
            view.writeMessage(String.format("Connected to database: <%s>", currentDatabaseName));
        } else if (manager.isConnected() && tableLayer) {
            view.writeMessage(String.format("Connected to database: <%s>. Selected table: <%s>",
                    currentDatabaseName, currentTableName));
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

    public void changeTableLayer(boolean tableLayer) {
        this.tableLayer = tableLayer;
    }

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
        changeTableLayer(true);
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }
}