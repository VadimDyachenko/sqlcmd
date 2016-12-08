package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;

public class Return implements Command {
    private final RunParameters runParameters;

    public Return(RunParameters runParameters) {
        this.runParameters = runParameters;
    }

    @Override
    public void execute() {
        runParameters.setTableLevel(false);
    }

}
