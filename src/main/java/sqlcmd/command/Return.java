package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;

/**
 * Created by vadim on 02.10.16.
 */
public class Return implements Command{
    private CommandExecutor commandExecutor;

    public Return(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void execute() throws InterruptOperationException {
        commandExecutor.setTableLevel(false);
        commandExecutor.setTableName("");
    }
}
