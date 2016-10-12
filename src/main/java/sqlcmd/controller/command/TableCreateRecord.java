package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TableCreateRecord implements Command {
    private Controller controller;
    private DatabaseManager manager;
    private View view;

    public TableCreateRecord(Controller controller, DatabaseManager manager, View view) {
        this.controller = controller;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String tableName = controller.getCurrentTableName();

        printInfo(tableName);
        List<String> columnNames = getAvailableColumnNames(tableName);
        printAvailableColumnNames(columnNames);

        String[] inputUserData = new String[0];
        while (true) {
            try {
                inputUserData = view.readLine().split("\\|");
                validateInputData(inputUserData, columnNames);
                break;
            } catch (IllegalArgumentException e) {
                view.writeMessage("Incorrect data. " + e.getMessage());
            }
        }
        DataSet dataSet = new DataSet();
        for (int index = 0; index < inputUserData.length; index += 2) {
            String columnName = inputUserData[index];
            String value = inputUserData[index + 1];
            dataSet.put(columnName, value);
        }
        try {
            manager.createTableRecord(tableName, dataSet);
            view.writeMessage(String.format(
                    "Record %s was createTableRecord successful in table <%s>\n", dataSet, tableName));
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

    }

    private List<String> getAvailableColumnNames(String tableName) {
        List<String> columnNames = new ArrayList<>();
        try {
            columnNames = manager.getTableColumnNames(tableName);
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }
        return columnNames;
    }

    private void validateInputData(String[] data, List<String> columnNames) {
        if (data.length % 2 != 0 || data.length / 2 != columnNames.size()) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }
    }

    private void printInfo(String tableName) {
        view.writeMessage("Enter data to createTableRecord table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n");
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));
    }

    private void printAvailableColumnNames(List<String> columnNames) {
        String names = "[";
        for (String columnName : columnNames) {
            names += columnName + ", ";
        }
        names = names.substring(0, names.length() - 2);
        names += "]";
        view.writeMessage(names);
    }
}