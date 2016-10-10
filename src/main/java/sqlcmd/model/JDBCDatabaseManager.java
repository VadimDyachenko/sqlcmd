package sqlcmd.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JDBCDatabaseManager implements DatabaseManager {
    private Connection connection;
    private final String DATABASE_HOST = "//192.168.1.5:5432/";  // Database host address, write:  //localhost:5432/
    private String currentDatabaseName;                          // for connect to local postgresql database
    private String currentTableName;
    private boolean tableLayer = false;

    @Override
    public void connect(String databaseName, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql:" + DATABASE_HOST + databaseName, user, password);
        currentDatabaseName = databaseName;
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public boolean isTableLayer() {
        return tableLayer;
    }

    @Override
    public void changeTableLayer(boolean tableLayer) {
        this.tableLayer = tableLayer;
    }

    @Override
    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    @Override
    public String getCurrentTableName() {
        return currentTableName;
    }

    @Override
    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
        changeTableLayer(true);
    }

    @Override
    public List<String> getAllTableNames() throws SQLException {
        checkConnection();

        List<String> resultTableNames = new LinkedList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT table_name FROM information_schema.tables " +
                             "WHERE table_schema='public' AND table_type='BASE TABLE'");
        ) {
            while (resultSet.next()) {
                resultTableNames.add(resultSet.getString("table_name"));
            }

            return resultTableNames;
        }
    }

    @Override
    public void createTableRecord(String tableName, DataSet users) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement()) {
            String columnName = getNameFormatted(users, "%s,");
            String values = getValuesFormatted(users, "'%s',");
            statement.executeUpdate("INSERT INTO public." +
                    tableName + "(" + columnName + ")" +
                    "VALUES (" + values + ")");
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM public." + tableName)
        ) {
            int size = getSize(tableName);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            DataSet[] result = new DataSet[size];
            int index = 0;
            while (resultSet.next()) {
                DataSet dataSet = new DataSet();
                result[index++] = dataSet;
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    dataSet.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
            }
            return result;
        }
    }

    @Override
    public void updateTableRecord(String tableName, int id, DataSet newValue) throws SQLException {
        checkConnection();
        String tableNames = getNameFormatted(newValue, "%s = ?,");
        String sql = "UPDATE public." + tableName + " SET " + tableNames + " WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int index = 1;
            for (Object value : newValue.getValues()) {
                ps.setObject(index, value);
                index++;
            }
            ps.setObject(index, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void clearCurrentTable() throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM public." + currentTableName);
        }
    }

    @Override
    public List<String> getTableColumnNames(String tableName) throws SQLException {
        checkConnection();
        List<String> resultList = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT column_name FROM information_schema.columns " +
                             "WHERE table_schema = 'public' and table_name = '" + tableName + "'")
        ) {
            while (resultSet.next()) {
                resultList.add(resultSet.getString("column_name"));
            }
            return resultList;
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    private String getNameFormatted(DataSet newValue, String format) {
        String string = "";
        for (String name : newValue.getNames()) {
            string += String.format(format, name);
        }
        string = string.substring(0, string.length() - 1);
        return string;
    }

    private String getValuesFormatted(DataSet input, String format) {
        String values = "";
        for (Object value : input.getValues()) {
            values += String.format(format, value);
        }
        values = values.substring(0, values.length() - 1);
        return values;
    }

    private int getSize(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM public." + tableName);
        resultSet.next();
        int size = resultSet.getInt("count");
        resultSet.close();
        return size;
    }

    private void checkConnection() throws SQLException {
        if (connection == null) {
            throw new SQLException("No connection to the database.");
        }
    }
}
