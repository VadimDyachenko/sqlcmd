package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;

public class TableClear implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;

    public TableClear(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {

        view.writeMessage(String.format("Do you really want to clear table <%s>? <y/n>", controller.getCurrentTableName()));
        String answer = view.readLine();

        if (answer.trim().toLowerCase().equals("y")) {
            try {
                manager.clearCurrentTable(controller.getCurrentTableName());
            } catch (SQLException e) {
                view.writeMessage("Table not clear, " + e.getMessage());
            }
            view.writeMessage("Table clear successful.\n");
        }
    }
}
