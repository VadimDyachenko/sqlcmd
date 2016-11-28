package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class Exit implements Command {
    private final DatabaseManager manager;
    private final View view;
    private ResourceBundle res;

    public Exit(RunParameters runParameters, DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "Exit");
    }

    @Override
    public void execute() throws ExitException {
        view.writeMessage(res.getString("exit.question"));
        String answer = view.readLine().trim().toLowerCase();
        if (answer.equals("y") || answer.equals("д")) {
            try {
                manager.disconnect();
            } catch (SQLException e) {
               //NOP
            }
            view.writeMessage(res.getString("exit.end"));
            throw new ExitException();
        }
    }
}
