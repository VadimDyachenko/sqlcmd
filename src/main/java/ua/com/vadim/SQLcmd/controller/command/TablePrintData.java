package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TablePrintData extends AbstractCommand {
    private final ResourceBundle resource;

    public TablePrintData(RunParameters parameters, DatabaseManager manager, View view) {
        super(parameters, manager, view);
        resource = ResourceBundle.getBundle("TablePrintData", new UTF8Control());
    }

    @Override
    public void execute() {
        String tableName = parameters.getTableName();
        try {
            DataSet[] tableData = getTableData(tableName);
            if (tableData.length == 0) {
                view.writeMessage(String.format(resource.getString("table.print.data.empty"), tableName));
            } else {
                printData(tableData);
            }
        } catch (SQLException e) {
            view.writeMessage(resource.getString("table.print.data.failed") + e.getMessage());
        }
    }

    private void printData(DataSet[] tableData) {
        Map<String, Integer> tableRowLength = getTableRowMaxLength(tableData);
        printHeader(tableRowLength);
        for (DataSet aTableData : tableData) {
            printRow(aTableData, tableRowLength);
        }
    }

    private DataSet[] getTableData(String tableName) throws SQLException {
        return manager.getTableData(tableName);
    }

    private Map<String, Integer> getTableRowMaxLength(DataSet[] tableData) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (String key : tableData[0].getNames()) {
            Integer length = key.length();
            for (DataSet data : tableData) {
                Integer lengthValue = data.get(key).toString().length();
                if (length < lengthValue) {
                    length = lengthValue;
                }
            }
            result.put(key, length);
        }
        return result;
    }

    /**
     * Method output column names in the format: |xxxx |yyy    |zz|
     * the width of the recording between "|" equal to the length of the longest entry in this column
     *
     * @param rowLength
     */
    private void printHeader(Map<String, Integer> rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String key : rowLength.keySet()) {
            sb.append(key);
            for (int i = (rowLength.get(key) - key.length()); i > 0; i--) {
                sb.append(" ");
            }
            sb.append('|');
        }
        view.writeMessage(sb.toString());
        printTableLine(rowLength);
    }

    /**
     * Method output entry in the format: |xxxx |yyy    |zz|
     * the width of the recording between "|" equal to the length of the longest entry in this column
     *
     * @param dataSet
     * @param rowLength
     */
    private void printRow(DataSet dataSet, Map<String, Integer> rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append('|');
        for (String key : rowLength.keySet()) {
            sb.append(dataSet.get(key));
            for (int i = (rowLength.get(key) - dataSet.get(key).toString().length()); i > 0; i--) {
                sb.append(" ");
            }
            sb.append('|');
        }
        view.writeMessage(sb.toString());
    }

    /**
     * Method output line: +-----+----+---+------+
     * The number of "+" depends on the number of columns in the table
     * the width of the line between "+" is the length of the longest entry in the column
     *
     * @param rowLength
     */
    private void printTableLine(Map<String, Integer> rowLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (String key : rowLength.keySet()) {
            for (int i = 0; i < rowLength.get(key); i++) {
                sb.append("-");
            }
            sb.append("+");
        }
        view.writeMessage(sb.toString());
    }
}