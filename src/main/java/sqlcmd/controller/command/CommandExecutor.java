package sqlcmd.controller.command;

import sqlcmd.controller.AvailableOperation;
import sqlcmd.controller.Controller;
import sqlcmd.model.DatabaseManager;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.view.View;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {

    private final Map<AvailableOperation, Command> commandMap = new HashMap<>();

    public CommandExecutor(Controller controller, DatabaseManager manager, View view) {
        commandMap.put(AvailableOperation.CONNECT, new Connect(controller, manager, view));
        commandMap.put(AvailableOperation.LIST_TABLE, new ListTableNames(controller, manager, view));
        commandMap.put(AvailableOperation.SELECT_TABLE, new SelectTable(controller, manager, view));
        commandMap.put(AvailableOperation.TABLE_PRINT, new TablePrintData(controller, manager, view));
        commandMap.put(AvailableOperation.TABLE_CREATE_RECORD, new TableCreateRecord(controller, manager, view));
        commandMap.put(AvailableOperation.TABLE_UPDATE_RECORD, new TableUpdateRecord(manager, view));
        commandMap.put(AvailableOperation.TABLE_CLEAR, new TableClear(controller, manager, view));
        commandMap.put(AvailableOperation.RETURN, new Return(controller));
        commandMap.put(AvailableOperation.EXIT, new Exit(manager, view));
    }

    public void execute(AvailableOperation availableOperation) throws InterruptOperationException {
        commandMap.get(availableOperation).execute();
    }
}
