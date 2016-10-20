package sqlcmd.controller.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

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
