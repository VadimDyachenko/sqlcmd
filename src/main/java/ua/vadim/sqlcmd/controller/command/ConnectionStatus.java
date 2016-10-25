package ua.vadim.sqlcmd.controller.command;

import ua.vadim.sqlcmd.exception.InterruptOperationException;
import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;
import ua.vadim.sqlcmd.model.DatabaseManager;
import ua.vadim.sqlcmd.view.View;

public class ConnectionStatus implements Command {

    private DatabaseManager manager;
    private View view;
    private ConnectionStatusHelper connectionStatusHelper;

    public ConnectionStatus(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String currentDatabaseName = connectionStatusHelper.getCurrentDatabaseName();
        String currentTableName = connectionStatusHelper.getCurrentTableName();

        if (manager.isConnected() && connectionStatusHelper.isTableLevel()) {
            view.writeMessage(String.format("Connected to database: <%s>. Selected table: <%s>",
                    currentDatabaseName, currentTableName));
        } else if (manager.isConnected() && !connectionStatusHelper.isTableLevel()){
            view.writeMessage(String.format("Connected to database: <%s>", currentDatabaseName));
        }
    }
}
