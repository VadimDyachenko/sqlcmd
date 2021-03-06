package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class TableClear extends AbstractCommand {
    private final ResourceBundle resource;

    public TableClear(RunParameters parameters, DatabaseManager manager, View view) {
        super(parameters, manager, view);
        resource = ResourceBundle.getBundle("TableClear", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        String currentTableName = parameters.getTableName();
        view.writeMessage(String.format(resource.getString("table.clear.question"), currentTableName));
        String answer = view.read().trim().toLowerCase();
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
