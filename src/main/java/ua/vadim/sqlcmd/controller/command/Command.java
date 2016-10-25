package ua.vadim.sqlcmd.controller.command;

import ua.vadim.sqlcmd.exception.InterruptOperationException;

public interface Command {

    void execute() throws InterruptOperationException;

}
