package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;
import SQLcmd.exception.InterruptOperationException;

public class Return implements Command {
    private RunParameters runParameters;

    public Return(RunParameters runParameters) {
        this.runParameters = runParameters;
    }

    @Override
    public void execute() throws InterruptOperationException {
        runParameters.setTableLevel(false);
    }

}
