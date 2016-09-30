package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.Arrays;

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
        printHeader(tableData[0]);
        int[] rowTableLength = new int[tableData.length];
        String[] columnNames = tableData[0].getNames();
        for (int index = 0; index < tableData.length; index++) {
            manager.getTableRowLenght(tableName);
        }
        System.out.println(Arrays.toString(rowTableLength));
    }

    private void printHeader(DataSet dataSet) {
        String[] columnNames = dataSet.getNames();
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String columnName : columnNames) {
            sb.append(columnName);
            sb.append('|');
        }
        sb.append("\n");
        view.writeMessage(sb.toString());
    }
}
