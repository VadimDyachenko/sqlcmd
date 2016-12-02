package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class TableUpdateRecord implements Command {
    private ResourceBundle res;
    private RunParameters runParameters;
    private DatabaseManager manager;
    private View view;

    public TableUpdateRecord(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "TableUpdateRecord", new UTF8Control());
    }

    @Override
    public void execute() {
        String tableName = runParameters.getTableName();
        Set<String> columnNames = getAvailableColumnNames(tableName);
        if (columnNames.isEmpty()) {
            view.writeMessage(String.format(res.getString("table.update.empty.table"), tableName));
        } else {
            printHelpInfo();
            printAvailableColumnNames(columnNames);
            updateRecord(tableName);
        }
    }

    private void printHelpInfo() {
        view.writeMessage(res.getString("table.update.record.help"));
    }

    private Set<String> getAvailableColumnNames(String tableName) {
        Set<String> columnNames = new LinkedHashSet<>();
        try {
            columnNames = manager.getTableColumnNames(tableName);
        } catch (SQLException e) {
            view.writeMessage(res.getString("table.update.record.failed") + e.getMessage());
        }
        return columnNames;
    }

    private void printAvailableColumnNames(Set<String> columnNames) {
        view.writeMessage(String.format(res.getString("table.update.available.column"), columnNames.toString()));
    }

    private void updateRecord(String tableName) {
        do {
            try {
                String[] inputRecordData = inputData(getAvailableColumnNames(tableName));
                DataSet dataSet = new DataSetImpl();
                Integer id = Integer.parseInt(inputRecordData[1]);
                for (int index = 2; index < inputRecordData.length; index += 2) {
                    String columnName = inputRecordData[index];
                    String value = inputRecordData[index + 1];
                    dataSet.put(columnName, value);
                }
                manager.updateTableRecord(tableName, id, dataSet);
                view.writeMessage(String.format(res.getString("table.update.successful"), dataSet));
                return;
            } catch (SQLException e) {
                view.writeMessage(res.getString("table.update.record.failed") + e.getMessage());
                view.writeMessage(res.getString("table.update.incorrect.data"));
            } catch (NumberFormatException e) { //for check id
                view.writeMessage(res.getString("table.update.record.number.format"));
            } catch (IllegalArgumentException e1) { //for check data
                view.writeMessage(res.getString("table.update.incorrect.data") + e1.getMessage());
            }
        } while (true);
    }

    private String[] inputData(Set<String> columnNames) throws IllegalArgumentException {
//        do {
//            try {
                String[] inputUserData = view.readLine().split("\\|");
                validateInputData(inputUserData, columnNames);
                return inputUserData;
//            } catch (IllegalArgumentException e) {
//                view.writeMessage(res.getString("table.update.incorrect.data") + e.getMessage());
//            }
//        } while (true);
    }

    private void validateInputData(String[] data, Set<String> columnNames) {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException(res.getString("table.update.record.invalid.number"));
        }
    }

}
