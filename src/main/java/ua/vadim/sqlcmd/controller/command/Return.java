package ua.vadim.sqlcmd.controller.command;

import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;
import ua.vadim.sqlcmd.exception.InterruptOperationException;

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
