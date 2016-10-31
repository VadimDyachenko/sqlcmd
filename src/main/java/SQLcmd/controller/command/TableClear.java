package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;
import SQLcmd.exception.InterruptOperationException;
import SQLcmd.model.DatabaseManager;
import SQLcmd.view.View;

import java.sql.SQLException;

public class TableClear implements Command {
    private RunParameters runParameters;
    private DatabaseManager manager;
    private View view;

    public TableClear(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String currentTableName = runParameters.getTableName();
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
