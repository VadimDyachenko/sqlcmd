package sqlcmd.command;

import sqlcmd.database.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;


public class ExitCommand implements Command {
    private final DatabaseManager manager;
    private final View view;

    public ExitCommand(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        manager.disconnect();
        view.writeMessage("Do you really want to exit? <y/n>");
        String answer = view.readLine();
        if (answer.trim().toLowerCase().equals("y")) {
            view.writeMessage("Thank you for using SQLCmd. Good luck.");
        }
    }
}
