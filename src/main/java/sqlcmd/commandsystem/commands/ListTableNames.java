package sqlcmd.commandsystem.commands;

import sqlcmd.commandsystem.Command;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.List;

/**
 * Created by vadim on 29.09.16.
 */
public class ListTableNames implements Command {
    private DatabaseManager manager;
    private View view;

    public ListTableNames(DatabaseManager manager, View view) {
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
        view.writeMessage("Available tables:");

        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";

        view.writeMessage(availableTables + "\n");
    }
}
