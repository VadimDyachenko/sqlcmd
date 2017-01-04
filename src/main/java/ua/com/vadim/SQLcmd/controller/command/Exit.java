package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class Exit extends AbstractCommand implements Command {
    private final DatabaseManager manager;
    private final View view;
    private final ResourceBundle resource;

    public Exit(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("Exit", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        view.writeMessage(resource.getString("exit.question"));
        String answer = readLine().trim().toLowerCase();
        if (answer.equals("y") || answer.equals("д")) { //TODO выпилить хардкод y/д
            try {
                manager.disconnect();
            } catch (SQLException e) {
               //NOP
            }
            throw new ExitException();
        }
    }

    @Override
    View getView() {
        return view;
    }
}
