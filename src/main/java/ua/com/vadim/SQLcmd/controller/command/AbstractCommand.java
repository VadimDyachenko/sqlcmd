package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

abstract class AbstractCommand implements Command {
    protected DatabaseManager manager;
    protected View view;
    protected RunParameters parameters;

    public AbstractCommand (RunParameters parameters, DatabaseManager manager, View view) {
        this.parameters = parameters;
        this.manager = manager;
        this.view = view;
    }

    abstract View getView();

    String readLine() throws ExitException {

        String result = getView().read();
        if ("exit".equalsIgnoreCase(result)) {
            throw new ExitException();
        }

        return result;
    }
}
