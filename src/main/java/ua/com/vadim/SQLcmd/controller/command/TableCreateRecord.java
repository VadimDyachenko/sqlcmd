package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;


public class TableCreateRecord implements Command {
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public TableCreateRecord(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() {
        String tableName = runParameters.getTableName();
        printHelpInfo(tableName);

        Set<String> columnNames = getAvailableColumnNames(tableName);
        if (columnNames.isEmpty()) {
            return;
        }
        printAvailableColumnNames(tableName, columnNames);

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
                    "Record %s was create successful in table <%s>\n", dataSet, tableName));
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

    private void printHelpInfo(String tableName) {
        view.writeMessage("Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n");
    }

    private void printAvailableColumnNames(String tableName, Set<String> columnNames) {
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));
        view.writeMessage(columnNames.toString());
    }
}