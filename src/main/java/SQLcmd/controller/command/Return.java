package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;

public class Return implements Command {
    private RunParameters runParameters;

    public Return(RunParameters runParameters) {
        this.runParameters = runParameters;
    }

    @Override
    public void execute() {
        runParameters.setTableLevel(false);
    }

}
