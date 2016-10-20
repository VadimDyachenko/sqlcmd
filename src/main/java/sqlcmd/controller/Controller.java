package sqlcmd.controller;

import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

public class Controller {

    private View view;
    private CommandExecutor commandExecutor;
    private ConnectionStatusHelper connectionHelper;

    public Controller(DatabaseManager manager, View view) {
        this.view = view;
        connectionHelper = new ConnectionStatusHelper();
        commandExecutor = new CommandExecutor(connectionHelper, manager, view);
    }

    public void run() {
        view.writeMessage("Welcome to SQLCmd!\n");
        try {
            do {
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
            while (true);
        } catch (ExitException e1) {
            //NOP
        } catch (InterruptOperationException e2) {
            view.writeMessage("Terminated. Thank you for using SQLCmd. Good luck.");
            //System.exit(0);  <--- закомментировано из-за тестов.
        }
    }

    private AvailableCommand askCommand() throws InterruptOperationException {

        commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);

        view.writeMessage("Please choose an operation desired or type 'EXIT' for exiting");

        while (true) {
            if (!connectionHelper.isTableLevel()) {
                printMainMenu();
            } else {
                printTableMenu();
            }

            String choice = view.readLine();

            try {
                Integer numOfChoice = Integer.parseInt(choice);
                if (connectionHelper.isTableLevel()) {
                    return AvailableCommand.getTableCommand(numOfChoice);
                } else {
                    return AvailableCommand.getMainCommand(numOfChoice);
                }
            } catch (IllegalArgumentException e) {
                view.writeMessage("\nPlease choise correct number:");
            }
        }
    }

    private void printMainMenu() {
        view.writeMessage(
                "1 - DBConnect to database\n" +
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