package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;

public class TableClear implements Command {
    private DatabaseManager manager;
    private View view;

    public TableClear(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {

        view.writeMessage(String.format("Do you really want to clear table <%s>? <y/n>", manager.getCurrentTableName()));
        String answer = view.readLine();

        if (answer.trim().toLowerCase().equals("y")) {
            try {
                manager.clearCurrentTable();
            } catch (SQLException e) {
                view.writeMessage("Table not clear, " + e.getMessage());
            }
            view.writeMessage("Table clear successful.\n");
        }
    }
}
