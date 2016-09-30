package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

/**
 * Created by vadim on 29.09.16.
 */
public class PrintTableData implements Command {
    private DatabaseManager manager;
    private View view;

    public PrintTableData(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        view.writeMessage("Enter table name");
        String tableName = view.readLine();
        DataSet[] tableData = manager.getTableData(tableName);
        String[] columnNames = tableData[0].getNames();


    }
}
