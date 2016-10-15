package sqlcmd.controller.command;

import sqlcmd.controller.Controller;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DataSetImpl;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;


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
        Set<String> columnNames = getAvailableColumnNames(tableName);
        printAvailableColumnNames(columnNames);

        String[] inputUserData;
        while (true) {
            try {
                inputUserData = view.readLine().split("\\|");
                validateInputData(inputUserData, columnNames);
                break;
            } catch (IllegalArgumentException e) {
                view.writeMessage("Incorrect data. " + e.getMessage());
            }
        }
        DataSet dataSet = new DataSetImpl();
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

    private Set<String> getAvailableColumnNames(String tableName) {
        Set<String> columnNames = new LinkedHashSet<>();
        try {
            columnNames = manager.getTableColumnNames(tableName);
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }
        return columnNames;
    }

    private void validateInputData(String[] data, Set<String> columnNames) {
        if (data.length % 2 != 0 || data.length / 2 != columnNames.size()) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }
    }

    private void printInfo(String tableName) {
        view.writeMessage("Enter data to createTableRecord table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n");
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));
    }

    private void printAvailableColumnNames(Set<String> columnNames) {
        view.writeMessage(columnNames.toString());
    }
}