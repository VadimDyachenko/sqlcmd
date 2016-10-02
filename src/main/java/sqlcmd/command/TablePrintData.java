package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.List;

public class TablePrintData implements Command {
    private DatabaseManager manager;
    private View view;
    private CommandExecutor commandExecutor;

    public TablePrintData(CommandExecutor commandExecutor, DatabaseManager manager, View view) {
        this.commandExecutor = commandExecutor;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String tableName = commandExecutor.getTableName();
        DataSet[] tableData = manager.getTableData(tableName);
        int[] tableRowLength = getTableRowMaxLenght(tableData);
        printHeader(tableData[0]);
        for (int i = 0; i < tableData.length; i++) {
            printRow(tableData[i]/*, tableRowLength*/);
        }
        view.writeMessage("\n");


//        int[] rowTableLength = new int[tableData.length];
//        String[] columnNames = tableData[0].getNames();

//        for (int index = 0; index < tableData.length; index++) {
//            manager.getTableRowMaxLenght(tableName);
//        }
//
//        System.out.println(Arrays.toString(rowTableLength));
    }

    private int[] getTableRowMaxLenght(DataSet[] tableData) {
        int[] result = new int[tableData[0].getNames().length];
        for (int i = 0; i < result.length; i++) {
            int maxLength = 0;
            for (int j = 0; j < tableData.length; j++) {
                int length = tableData[i].getValues()[j].toString().length();
                if (maxLength < length) {
                    maxLength = length;
                }
            }
            result[i] = maxLength;
        }
        return result;
    }

    private void printHeader(DataSet dataSet) {
        String[] columnNames = dataSet.getNames();
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String columnName : columnNames) {
            sb.append(columnName);
            sb.append('|');
        }
        view.writeMessage(sb.toString());
    }

    private void printRow(DataSet dataSet) {
        Object[] columnValues = dataSet.getValues();
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (Object columnValue : columnValues) {
            sb.append(columnValue);
            sb.append('|');
        }
        view.writeMessage(sb.toString());
    }
}