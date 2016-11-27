package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.util.ResourceBundle;

public class ConnectionStatus implements Command {

    private final ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public ConnectionStatus(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "connectionStatus");
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage(res.getString("connection.status.without.connection"));
            return;
        }

        String message = String.format(res.getString("connection.status.database"), runParameters.getDatabaseName());
        message += " ";
        if (runParameters.isTableLevel()) {
            message += String.format(res.getString("connection.status.table"), runParameters.getTableName());
        }
//        message += "\n";
        view.writeMessage(message);
    }
}
