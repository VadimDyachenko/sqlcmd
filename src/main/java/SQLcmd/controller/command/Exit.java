package SQLcmd.controller.command;

import SQLcmd.exception.ExitException;
import SQLcmd.model.DatabaseManager;
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
    public void execute() {
        view.writeMessage("Do you really want to exit? <y/n>");

        String answer = view.readLine();

        if (answer.trim().toLowerCase().equals("y")) {
            try {
                if (manager.isConnected()) {
                    manager.disconnect();
                }
            } catch (SQLException e) {
                view.writeMessage(e.getMessage());          //TODO причесать этот блок
                view.writeMessage("Thank you for using SQLCmd. Good luck.");
                throw new ExitException();
            }
        }
    }
}
