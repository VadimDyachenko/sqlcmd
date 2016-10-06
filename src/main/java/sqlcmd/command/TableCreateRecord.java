package sqlcmd.command;

import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.List;


public class TableCreateRecord implements Command {
    private DatabaseManager manager;
    private View view;

    public TableCreateRecord(DatabaseManager manager, View view) {
        this.manager = manager;
        this.view = view;
    }

    @Override
    public void execute() throws InterruptOperationException {
        String tableName = manager.getCurrentTableName();

        view.writeMessage("Enter data to createTableRecord table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n");
        view.writeMessage(String.format("Available column name for table <%s> :", tableName));

        try {
            List<String> columnNames = manager.getTableColumnNames(tableName);
            printAvailableColumnNames(columnNames);
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        String[] data = view.readLine().split("\\|");
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }

        DataSet dataSet = new DataSet();
        for (int index = 0; index < data.length; index += 2) {
            String columnName = data[index];
            String value = data[index + 1];
            dataSet.put(columnName, value);
        }
        try {
            manager.createTableRecord(tableName, dataSet);
        } catch (SQLException e) {
            view.writeMessage("Failure, because " + e.getMessage());
        }

        view.writeMessage(String.format(
                "Record %s was createTableRecord successful in table <%s>\n", dataSet, tableName));
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
