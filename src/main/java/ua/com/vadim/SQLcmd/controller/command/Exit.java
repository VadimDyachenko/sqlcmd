package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class Exit extends AbstractCommand {

    private final ResourceBundle resource;

    public Exit(RunParameters parameters, DatabaseManager manager, View view) {
        super(parameters, manager, view);
        resource = ResourceBundle.getBundle("Exit", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        view.writeMessage(resource.getString("exit.question"));
        String answer = readLine().trim().toLowerCase();
        if (answer.equals(resource.getString("exit.yes"))) {
            try {
                manager.disconnect();
            } catch (SQLException e) {
               //NOP
            }
            throw new ExitException();
        }
    }
}
