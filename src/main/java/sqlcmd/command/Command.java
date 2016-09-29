package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;

public interface Command {

    void execute() throws InterruptOperationException;

}
