package sqlcmd.command;

import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<Operation, Command> commandMap = new HashMap<>();
    private DatabaseManager manager;
    private View view;
    private String databaseName;
    private String tableName;
    private boolean tableLevel = false;

    public CommandExecutor(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        commandMap.put(Operation.CONNECT, new Connect(this, manager, view));
        commandMap.put(Operation.LIST_TABLE, new ListTableNames(manager, view));
        commandMap.put(Operation.SELECT_TABLE, new SelectTable(this, manager, view));
        commandMap.put(Operation.TABLE_PRINT, new TablePrintData(this, manager, view));
        commandMap.put(Operation.TABLE_CREATE_RECORD, new TableCreateRecord(this, manager, view));
        commandMap.put(Operation.TABLE_CLEAR, new TableClear(this, manager, view));
        commandMap.put(Operation.RETURN, new Return(this));
        commandMap.put(Operation.EXIT, new Exit(manager, view));
    }

    public void execute(Operation operation) throws InterruptOperationException {
        commandMap.get(operation).execute();
    }

    public Operation askMainOperation() throws InterruptOperationException {
        if (manager.isConnected() && !tableLevel) {
            view.writeMessage(String.format("Connected to database: <%s>", databaseName));
        } else if (manager.isConnected() && tableLevel) {
            view.writeMessage(String.format("Connected to database: <%s>. Selected table: <%s>", databaseName, tableName));
        }
        view.writeMessage("Please choose an operation desired or type 'EXIT' for exiting");
        while (true) {
            if (!tableLevel) {
                view.writeMessage("1 - Connect to database");
                view.writeMessage("2 - List all table names");
                view.writeMessage("3 - Select table to work");
                view.writeMessage("4 - Exit");
            } else {
                view.writeMessage("1 - Print table data");
                view.writeMessage("2 - Change table records");
                view.writeMessage("3 - Clear table");
                view.writeMessage("4 - Return to previous menu");
            }
            String choice = view.readLine();
            try {
                Integer numOfChoice = Integer.parseInt(choice);
                if (tableLevel) {
                    return Operation.getTableOperation(numOfChoice);
                } else {
                    return Operation.getMainOperation(numOfChoice);
                }
            } catch (IllegalArgumentException e) {
                view.writeMessage("\nPlease choise correct number:");
            }
        }
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.tableLevel = true;
    }

    public void resetTableLevel() {
        tableLevel = false;
    }
}
