package sqlcmd.command;

import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<Operation, Command> commandMap = new HashMap<>();
    DatabaseManager manager;
    View view;

    public CommandExecutor(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        commandMap.put(Operation.CONNECT, new Connect(manager, view));
        commandMap.put(Operation.LIST_TABLE, new ListTableNames(manager, view));
        commandMap.put(Operation.TABLE_PRINT, new TablePrintData(manager, view));
//        commandMap.put(Operation.TABLE_CHANGE, new Connect(manager, consoleHelper));
        commandMap.put(Operation.EXIT, new Exit(manager, view));
    }

    public void execute(Operation operation) throws InterruptOperationException {
        commandMap.get(operation).execute();
    }

    public Operation askMainOperation() throws InterruptOperationException {
        if (manager.isConnected()) {
            view.writeMessage(String.format("Connected to database: %s", manager.getDatabaseName()));
        }

        view.writeMessage("Please choose an operation desired or type 'EXIT' for exiting");
        while (true) {
            view.writeMessage("1 - Connect to database");
            view.writeMessage("2 - List all table names");
            view.writeMessage("3 - Select table to work");
            view.writeMessage("4 - Exit");

            String choice = view.readLine();
            try {
                Integer numOfChoice = Integer.parseInt(choice);
                return Operation.getMainOperation(numOfChoice);
            } catch (IllegalArgumentException e) {
                view.writeMessage("\nPlease choise correct number:");
            }
        }
    }
}
