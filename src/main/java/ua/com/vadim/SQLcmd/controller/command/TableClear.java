package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

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
    public void execute() {
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
