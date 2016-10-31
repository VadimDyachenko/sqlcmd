package SQLcmd.controller.command;

import SQLcmd.exception.ExitException;
import SQLcmd.model.DatabaseManager;
import SQLcmd.exception.InterruptOperationException;
import SQLcmd.view.View;

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
            } finally {
            view.writeMessage("Thank you for using SQLCmd. Good luck.");
//          System.exit(0);  // <-- For normal use
            throw new ExitException(); // <-- For integration test
            }
        }
    }
}
