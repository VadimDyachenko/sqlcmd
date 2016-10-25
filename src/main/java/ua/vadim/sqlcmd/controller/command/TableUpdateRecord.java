package ua.vadim.sqlcmd.controller.command;

import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;
import ua.vadim.sqlcmd.exception.InterruptOperationException;
import ua.vadim.sqlcmd.model.DatabaseManager;
import ua.vadim.sqlcmd.view.View;

public class TableUpdateRecord implements Command {
    private ConnectionStatusHelper connectionStatusHelper;
    private DatabaseManager manager;
    private View view;

    public TableUpdateRecord(ConnectionStatusHelper connectionStatusHelper, DatabaseManager manager, View view) {
        this.connectionStatusHelper = connectionStatusHelper;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String tableName = connectionStatusHelper.getCurrentTableName();
        printHelpInfo(tableName);

    }

    private void printHelpInfo(String tableName) {
        view.writeMessage("Enter data to update table record.\n" +
                "Input format: id|Value1|ColumnName1|Value1| ... |ColumnNameN|ValueN\n");
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));
    }
}
