package SQLcmd.controller;

import SQLcmd.exception.ExitException;
import SQLcmd.model.DatabaseManager;
import SQLcmd.model.PostgresDBManager;
import SQLcmd.view.Console;
import SQLcmd.view.View;

public class Controller {

    private View view;
    private CommandExecutor commandExecutor;
    private RunParameters runParameters;

    public void run() {
        try {
            setUp();
            view.writeMessage("Welcome to SQLCmd!\n");
            do {
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
            while (true);
        } catch (ExitException e) {
            //NOP
        }
    }

    private AvailableCommand askCommand() throws ExitException {
        commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);
        view.writeMessage("Please choose an operation desired");
        while (true) {
            try {
                printMenu();
                String choice = view.readLine();
                Integer numOfChoice = Integer.parseInt(choice);
                return runParameters.isTableLevel()
                        ? AvailableCommand.getTableCommand(numOfChoice)
                        : AvailableCommand.getMainCommand(numOfChoice);

            } catch (IllegalArgumentException e) {
                view.writeMessage("\nPlease choice correct number:");
            }
        }
    }

    private void printMenu() {
        if (runParameters.isTableLevel()) {
            printTableMenu();
        } else {
            printMainMenu();
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

    private void setUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        view = new Console();
        DatabaseManager manager = new PostgresDBManager(runParameters.getDriver(),
                runParameters.getServerIP(),
                runParameters.getServerPort());
        commandExecutor = new CommandExecutor(runParameters, manager, view);
    }
}
