package sqlcmd.command;

import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;


public class Exit implements Command {
    private final DatabaseManager manager;
    private final View view;

    public Exit(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        view.writeMessage("Do you really want to exit? <y/n>");
        String answer = view.readLine();
        if (answer.trim().toLowerCase().equals("y")) {
            view.writeMessage("Thank you for using SQLCmd. Good luck.");
            if(manager.isConnected()) {
                manager.disconnect();
            }
            throw new ExitException();
        }
    }
}
