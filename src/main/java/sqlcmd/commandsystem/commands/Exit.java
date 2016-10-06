package sqlcmd.commandsystem.commands;

import sqlcmd.commandsystem.Command;
import sqlcmd.exception.ExitException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.sql.SQLException;


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
            try {
                if (manager.isConnected()) {
                    manager.disconnect();
                }
            } catch (SQLException e) {
                view.writeMessage(e.getMessage());
            }
            view.writeMessage("Thank you for using SQLCmd. Good luck.");

//            System.exit(0);  // <-- For normal use
            throw new ExitException(); // <-- For integration test
        }
    }
}
