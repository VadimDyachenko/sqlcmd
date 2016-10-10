package sqlcmd.controller.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

public class TableUpdateRecord implements Command {
    private DatabaseManager manager;
    private View view;

    public TableUpdateRecord(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {

    }
}
