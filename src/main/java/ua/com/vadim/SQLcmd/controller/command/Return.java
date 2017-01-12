package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

public class Return extends AbstractCommand {

    public Return(RunParameters parameters, DatabaseManager manager, View view) {
        super(parameters, manager, view);
    }

    @Override
    public void execute() {
        parameters.setTableLevel(false);
    }
}
