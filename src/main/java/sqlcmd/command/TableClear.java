package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

/**
 * Created by vadim on 04.10.16.
 */
public class TableClear implements Command {
    CommandExecutor commandExecutor;
    DatabaseManager manager;
    View view;

    public TableClear(CommandExecutor commandExecutor, DatabaseManager manager, View view) {
        this.commandExecutor = commandExecutor;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        manager.clear(commandExecutor.getTableName());
        view.writeMessage("Table cler successful.\n");
    }
}
