package sqlcmd.command;

import sqlcmd.view.ConsoleHelper;
import sqlcmd.database.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;


public class ExitCommand implements Command {
    private final DatabaseManager manager;
    private final ConsoleHelper consoleHelper;

    public ExitCommand(DatabaseManager manager, ConsoleHelper consoleHelper) {
        this.manager = manager;
        this.consoleHelper = consoleHelper;
    }

    @Override
    public void execute() throws InterruptOperationException {
        manager.disconnect();
        consoleHelper.writeMessage("Do you really want to exit? <y/n>");
        String answer = consoleHelper.readLine();
        if (answer.trim().toLowerCase().equals("y")) {
            consoleHelper.writeMessage("Thank you for using SQLCmd. Good luck.");
        }
    }
}
