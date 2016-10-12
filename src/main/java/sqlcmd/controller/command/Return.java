package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;

public class Return implements Command {
    private Controller controller;

    public Return(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void execute() throws InterruptOperationException {
        controller.changeTableLayer(false);
    }

}
