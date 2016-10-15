package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DataSetImpl;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.*;

public class TablePrintData implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;


    public TablePrintData(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String tableName = controller.getCurrentTableName();
        DataSet[] tableData = new DataSetImpl[0];
        try {
            tableData = manager.getTableData(tableName);
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        if (tableData.length == 0) {
            view.writeMessage(String.format("Table <%s> is empty.\n", tableName));
            return;
        }

        int[] tableRowLength = getTableRowMaxLength(tableData);
        printHeader(tableData[0], tableRowLength);
        for (DataSet aTableData : tableData) {
            printRow(aTableData, tableRowLength);
        }
        view.writeMessage("\n");
    }

    private int[] getTableRowMaxLength(DataSet[] tableData) {

        int[] result = new int[tableData[0].getNames().size()];
        int index = 0;
        for (String key : tableData[0].getNames()) {
            int length = key.length();
            for (DataSet data : tableData) {
                int lengthValue = data.get(key).toString().length();
                if (length < lengthValue) {
                    length = lengthValue;
                }
            }
            result[index] = length;
            index++;
        }
        return result;
    }

    private void printHeader(DataSet dataSet, int[] rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        int index = 0;
        for (String key : dataSet.getNames()) {
            sb.append(key);
            for (int i = (rowLength[index]- key.length()); i > 0; i--) {
                sb.append(" ");
            }
            index++;
            sb.append('|');
        }
        view.writeMessage(sb.toString());
        printTableLine(rowLength);
    }


    private void printRow(DataSet dataSet, int[] rowLength) {

        StringBuilder sb = new StringBuilder();
        sb.append('|');
        int index = 0;
        for (Object values : dataSet.getValues()) {
            sb.append(values);
            for (int i = (rowLength[index] - values.toString().length()); i > 0; i--){
                sb.append(" ");
            }
            sb.append('|');
            index++;
        }
        view.writeMessage(sb.toString());
    }

    private void printTableLine(int[] rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int index = 0; index < rowLength.length; index++) {
            for (int i = 0; i < rowLength[index]; i++) {
                sb.append("-");
            }
            sb.append("+");
        }
        view.writeMessage(sb.toString());
    }
}
