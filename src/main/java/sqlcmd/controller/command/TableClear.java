package sqlcmd.controller.command;

import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;

public class TableClear implements Command {
    private ConnectionStatusHelper connectionStatusHelper;
    private DatabaseManager manager;
    private View view;

    public TableClear(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String currentTableName = connectionStatusHelper.getCurrentTableName();
        view.writeMessage(String.format("Do you really want to clear table <%s>? <y/n>", currentTableName));
        String answer = view.readLine();
        if (answer.trim().toLowerCase().equals("y")) {
            try {
                manager.clearCurrentTable(currentTableName);
            } catch (SQLException e) {
                view.writeMessage("Table not clear, " + e.getMessage());
                return;
            }
            view.writeMessage("Table clear successful.\n");
        }
    }
}
