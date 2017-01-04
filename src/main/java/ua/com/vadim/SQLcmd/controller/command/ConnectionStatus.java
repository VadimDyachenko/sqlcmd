package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.util.ResourceBundle;

public class ConnectionStatus implements Command {

    private final ResourceBundle resource;
    private final DatabaseManager manager;
    private final View view;
    private final RunParameters parameters;

    public ConnectionStatus(RunParameters parameters, DatabaseManager manager, View view) {
        this.parameters = parameters;
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("connectionStatus", new UTF8Control());
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage(resource.getString("connection.status.without.connection"));
            return;
        }
        String message = String.format(resource.getString("connection.status.database"), parameters.getDatabaseName());
        message += " ";

        if (parameters.isTableLevel()) {
            message += String.format(resource.getString("connection.status.table"), parameters.getTableName());
        }
        message += "\n";
        view.writeMessage(message);
    }
}
