package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.Arrays;

/**
 * Created by vadim on 29.09.16.
 */
public class ListTableNamesCommand implements Command {
    private DatabaseManager manager;
    private View view;

    public ListTableNamesCommand(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String[] tableNames = manager.getAllTableNames();
        view.writeMessage("\n" + Arrays.toString(tableNames) + "\n");
    }
}
