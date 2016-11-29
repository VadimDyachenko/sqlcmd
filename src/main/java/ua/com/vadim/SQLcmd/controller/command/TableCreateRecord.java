package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class TableCreateRecord implements Command {
    private ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;

    public TableCreateRecord(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "TableCreateRecord", new UTF8Control());
    }

    @Override
    public void execute() {
        String tableName = runParameters.getTableName();
        Set<String> columnNames = getAvailableColumnNames(tableName);
        if (columnNames.isEmpty()) {
            view.writeMessage(String.format(res.getString("table.create.empty.table"), tableName));
        } else {
            printHelpInfo();
            printAvailableColumnNames(columnNames);
            createRecord(tableName, inputRecordData(columnNames));
        }
    }

    private String[] inputRecordData(Set<String> columnNames) {
        do {
            try {
                String[] inputUserData = view.readLine().split("\\|");
                validateInputData(inputUserData, columnNames);
                return inputUserData;
            } catch (IllegalArgumentException e) {
                view.writeMessage(res.getString("table.create.incorrect.data") + e.getMessage());
            }
        } while (true);
    }

    private void createRecord(String tableName, String[] inputUserData) {
        DataSet dataSet = new DataSetImpl();
        for (int index = 0; index < inputUserData.length; index += 2) {
            String columnName = inputUserData[index];
            String value = inputUserData[index + 1];
            dataSet.put(columnName, value);
        }
        try {
            manager.createTableRecord(tableName, dataSet);
            view.writeMessage(String.format(res.getString("table.create.successful"), dataSet));
        } catch (SQLException e) {
            view.writeMessage(res.getString("table.create.record.failed") + e.getMessage());
        }
    }

    private Set<String> getAvailableColumnNames(String tableName) {
        Set<String> columnNames = new LinkedHashSet<>();
        try {
            columnNames = manager.getTableColumnNames(tableName);
        } catch (SQLException e) {
            view.writeMessage(res.getString("table.create.record.failed") + e.getMessage());
        }
        return columnNames;
    }

    private void validateInputData(String[] data, Set<String> columnNames) {
        if (data.length % 2 != 0 || data.length / 2 != columnNames.size()) {
            throw new IllegalArgumentException(res.getString("table.create.record.invalid.number"));
        }
    }

    private void printHelpInfo() {
        view.writeMessage(res.getString("table.create.record.help"));
    }

    private void printAvailableColumnNames(Set<String> columnNames) {
        view.writeMessage(String.format(res.getString("table.create.available.column"), columnNames.toString()));
    }
}