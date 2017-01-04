package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Set;

public class TableCreateRecord extends AbstractCommand implements Command {
    private final ResourceBundle resource;
    private final DatabaseManager manager;
    private final View view;
    private final RunParameters runParameters;

    public TableCreateRecord(RunParameters runParameters, DatabaseManager manager, View view) {
        this.runParameters = runParameters;
        this.manager = manager;
        this.view = view;
        resource = ResourceBundle.getBundle("TableCreateRecord", new UTF8Control());
    }

    @Override
    public void execute() throws ExitException {
        String tableName = runParameters.getTableName();
        try {
            Set<String> columnNames = getAvailableColumnNames(tableName);
            if (columnNames.isEmpty()) {
                view.writeMessage(String.format(resource.getString("table.create.empty.table"), tableName));
            } else {
                printHelpInfo();
                printAvailableColumnNames(columnNames);
                createRecord(tableName, inputRecordData(columnNames));
            }
        } catch (SQLException e) {
            view.writeMessage(resource.getString("table.create.record.failed") + " " + e.getMessage());
        }
    }

    private String[] inputRecordData(Set<String> columnNames) throws ExitException {
        do {
            try {
                String[] inputUserData = readLine().split("\\|");
                validateInputData(inputUserData, columnNames);
                return inputUserData;
            } catch (IllegalArgumentException e) {
                view.writeMessage(resource.getString("table.create.incorrect.data") + e.getMessage());
            }
        } while (true);
    }

    private void createRecord(String tableName, String[] inputUserData) throws SQLException {
        DataSet dataSet = new DataSetImpl();
        for (int index = 0; index < inputUserData.length; index += 2) {
            String columnName = inputUserData[index];
            String value = inputUserData[index + 1];
            dataSet.put(columnName, value);
        }
        manager.createTableRecord(tableName, dataSet);
        view.writeMessage(String.format(resource.getString("table.create.successful"), dataSet));
    }

    private Set<String> getAvailableColumnNames(String tableName) throws SQLException {
        return manager.getTableColumnNames(tableName);
    }

    private void validateInputData(String[] data, Set<String> columnNames) {
        if (data.length % 2 != 0 || data.length / 2 != columnNames.size()) {
            throw new IllegalArgumentException(resource.getString("table.create.record.invalid.number"));
        }
    }

    private void printHelpInfo() {
        view.writeMessage(resource.getString("table.create.record.help"));
    }

    private void printAvailableColumnNames(Set<String> columnNames) {
        view.writeMessage(String.format(resource.getString("table.create.available.column"), columnNames.toString()));
    }

    @Override
    View getView() {
        return view;
    }
}