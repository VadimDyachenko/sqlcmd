package sqlcmd.controller.command;

import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.exception.InterruptOperationException;

public class Return implements Command {
    private ConnectionStatusHelper connectionStatusHelper;

    public Return(ConnectionStatusHelper connectionStatusHelper) {
        this.connectionStatusHelper = connectionStatusHelper;
    }

    @Override
    public void execute() throws InterruptOperationException {
        connectionStatusHelper.setTableLevel(false);
    }

}
