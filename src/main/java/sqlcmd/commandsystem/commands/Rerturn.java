package sqlcmd.commandsystem.commands;

import sqlcmd.commandsystem.Command;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

/**
 * Created by Vadim on 05.10.2016.
 */
public class Rerturn implements Command {
    private DatabaseManager manager;
    View view;

    public Rerturn(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        manager.changeTableLayer(false);
    }

}
