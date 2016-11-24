package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.exception.ExitException;

public interface Command {
    void execute() throws ExitException;
}
