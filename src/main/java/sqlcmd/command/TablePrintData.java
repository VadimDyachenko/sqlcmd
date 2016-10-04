package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

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
        if (tableData.length == 0) {
            view.writeMessage(String.format("Table <%s> is empty.\n", tableName));
            return;
        }
        int[] tableRowLength = getTableRowMaxLenght(tableData);
        printHeader(tableData[0], tableRowLength);
        for (int i = 0; i < tableData.length; i++) {
            printRow(tableData[i], tableRowLength);
        }
        view.writeMessage("\n");
    }

    private int[] getTableRowMaxLenght(DataSet[] tableData) {
        int[] result = new int[tableData[0].getNames().length];
        for (int i = 0; i < result.length; i++) {
            int length = tableData[0].getNames()[i].length();;
            for (int j = 0; j < tableData.length; j++) {
                int lengthValue = tableData[j].getValues()[i].toString().length();
                if (length < lengthValue) {
                    length = lengthValue;
                }
            }
            result[i] = length;
        }
        return result;
    }

    private void printHeader(DataSet dataSet, int[] rowLength) {
        String[] columnNames = dataSet.getNames();
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (int i = 0; i < columnNames.length; i++) {
            sb.append(columnNames[i]);
            for (int j = (rowLength[i] - columnNames[i].length()); j > 0; j--) {
                sb.append(" ");
            }
            sb.append('|');
        }
        view.writeMessage(sb.toString());
        printTableLine(rowLength);
    }


    private void printRow(DataSet dataSet, int[] rowLength) {
        Object[] columnValues = dataSet.getValues();
        StringBuilder sb = new StringBuilder();
        sb.append('|');

        for (int i = 0; i < columnValues.length; i++) {
            sb.append(columnValues[i]);
            for (int j = (rowLength[i] - columnValues[i].toString().length()); j > 0; j--) {
                sb.append(" ");
            }
            sb.append('|');
        }
        view.writeMessage(sb.toString());
    }

    private void printTableLine(int[] rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int length : rowLength) {
            for (int j = 0; j < length; j++) {
                sb.append("-");
            }
            sb.append("+");
        }
        view.writeMessage(sb.toString());
    }
}
