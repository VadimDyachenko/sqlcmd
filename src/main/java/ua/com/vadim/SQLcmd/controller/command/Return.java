package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;

public class Return implements Command {
    private final RunParameters parameters;

    public Return(RunParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    public void execute() {
        parameters.setTableLevel(false);
    }

}
