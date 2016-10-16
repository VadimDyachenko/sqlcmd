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

        view.writeMessage(String.format("Do you really want to clear table <%s>? <y/n>", connectionStatusHelper.getCurrentTableName()));
        String answer = view.readLine();

        if (answer.trim().toLowerCase().equals("y")) {
            try {
                manager.clearCurrentTable(connectionStatusHelper.getCurrentTableName());
            } catch (SQLException e) {
                view.writeMessage("Table not clear, " + e.getMessage());
            }
            view.writeMessage("Table clear successful.\n");
        }
    }
}
