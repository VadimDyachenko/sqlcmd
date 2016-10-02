package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

/**
 * Created by vadim on 02.10.16.
 */
public class TableCreateRecord implements Command {
    private DatabaseManager manager;
    private View view;

    public TableCreateRecord(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        //TODO implements me!!!
    }
}
