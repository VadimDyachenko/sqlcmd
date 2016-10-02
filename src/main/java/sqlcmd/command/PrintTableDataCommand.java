package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;
import java.util.Arrays;
import java.util.Map;

public class PrintTableDataCommand implements Command {
    private DatabaseManager manager;
    private View view;

    public PrintTableDataCommand(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        view.writeMessage("Enter table name");
        String tableName = view.readLine();
        DataSet[] tableData = manager.getTableData(tableName);
        Map<String, Integer> tableRowLength = manager.getTableRowLenght(tableName);

        printRow(tableData[0], tableRowLength);

        int[] rowTableLength = new int[tableData.length];
        String[] columnNames = tableData[0].getNames();

        for (int index = 0; index < tableData.length; index++) {
            manager.getTableRowLenght(tableName);
        }

        System.out.println(Arrays.toString(rowTableLength));
    }

    private void printRow(DataSet dataSet, Map<String, Integer> table) {
        String[] columnNames = dataSet.getNames();
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String columnName : columnNames) {
            Integer lenhth;
            sb.append(columnName);
            sb.append('|');
        }
        sb.append("\n");
        view.writeMessage(sb.toString());
    }
}