package SQLcmd.controller.command;

import SQLcmd.exception.ExitException;

public interface Command {
    void execute() throws ExitException;
}
