package SQLcmd.controller.command;

import SQLcmd.exception.InterruptOperationException;

public interface Command {

    void execute() throws InterruptOperationException;

}
