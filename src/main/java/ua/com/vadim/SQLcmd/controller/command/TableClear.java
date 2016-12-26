package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class TableClear implements Command {
    private final ResourceBundle resource;
    private final RunParameters runParameters;
    private final DatabaseManager manager;
    private final View view;

    public TableClear(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("TableClear", new UTF8Control());
    }

    @Override
    public void execute() {
        String currentTableName = runParameters.getTableName();
        view.writeMessage(String.format(resource.getString("table.clear.question"), currentTableName));
        String answer = view.readLine().trim().toLowerCase();
        if (answer.equals("y") || answer.equals("д")) {
            try {
                manager.clearTable(currentTableName);
            } catch (SQLException e) {
                view.writeMessage(resource.getString("table.clear.failed") + e.getMessage());
                return;
            }
            view.writeMessage(resource.getString("table.clear.successful"));
        }
    }
}
