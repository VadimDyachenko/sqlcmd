package SQLcmd.controller.command;

import SQLcmd.controller.RunParameters;
import SQLcmd.model.DataSet;
import SQLcmd.model.DataSetImpl;
import SQLcmd.model.DatabaseManager;
import SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TablePrintData implements Command {
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;


    public TablePrintData(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        String tableName = runParameters.getTableName();
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

        Map<String, Integer> tableRowLength = getTableRowMaxLength(tableData);
        printHeader(tableRowLength);
        for (DataSet aTableData : tableData) {
            printRow(aTableData, tableRowLength);
        }
        view.writeMessage("\n");
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