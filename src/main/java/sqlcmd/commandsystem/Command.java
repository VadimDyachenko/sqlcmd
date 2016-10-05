package sqlcmd.commandsystem;

import sqlcmd.exception.InterruptOperationException;

public interface Command {

    void execute() throws InterruptOperationException;

}
