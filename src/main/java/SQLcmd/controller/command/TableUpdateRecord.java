package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;
import SQLcmd.model.DatabaseManager;
import SQLcmd.view.View;

public class TableUpdateRecord implements Command {
    private RunParameters runParameters;
    private DatabaseManager manager;
    private View view;

    public TableUpdateRecord(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        String tableName = runParameters.getTableName();
        printHelpInfo(tableName);
        //TODO дописать команду

    }

    private void printHelpInfo(String tableName) {
        view.writeMessage("Enter data to update table record.\n" +
                "Input format: id|Value1|ColumnName1|Value1| ... |ColumnNameN|ValueN\n");
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));
    }
}
