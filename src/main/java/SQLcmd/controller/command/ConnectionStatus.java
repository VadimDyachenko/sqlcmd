package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;
import SQLcmd.model.DatabaseManager;
import SQLcmd.view.View;

public class ConnectionStatus implements Command {

    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public ConnectionStatus(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        if (!manager.isConnected()) {
            view.writeMessage("No any database connected.");
            return;
        }

        String message = String.format("Connected to database: <%s>.", runParameters.getDatabaseName());
        if (runParameters.isTableLevel()) {
            message += String.format(" Selected table: <%s>", runParameters.getTableName());
        }
        view.writeMessage(message);
    }
}
