package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.view.View;

abstract class AbstractCommand implements Command {

    abstract View getView();

    String readLine() throws ExitException {

        String result = getView().read();
        if ("exit".equalsIgnoreCase(result)) {
            throw new ExitException();
        }

        return result;
    }
}
