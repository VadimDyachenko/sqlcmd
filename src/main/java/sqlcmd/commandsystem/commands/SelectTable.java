package sqlcmd.commandsystem.commands;

import sqlcmd.commandsystem.Command;
import sqlcmd.commandsystem.CommandExecutor;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.List;

/**
 * Created by vadim on 02.10.16.
 */
public class SelectTable implements Command {
    private DatabaseManager manager;
    private View view;

    public SelectTable(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        if (!manager.isConnected()) {
            view.writeMessage("No one connection to database. Select \"Connect to database\" first.\n");
            return;
        }

        List<String> tableNames = manager.getAllTableNames();
        view.writeMessage("Enter table name. Available tables:");
        printAvailableTables(tableNames);

        while (true) {
            String tableName = view.readLine();
            if (tableNames.contains(tableName)) {
                manager.setCurrentTableName(tableName);
                manager.changeTableLayer(true);
                break;
            } else {
                view.writeMessage("Enter correct table name. Available tables:");
                printAvailableTables(tableNames);
            }
        }
    }

    private void printAvailableTables(List<String> tableNames) {
        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";
        view.writeMessage(availableTables + "\n");
    }

}
