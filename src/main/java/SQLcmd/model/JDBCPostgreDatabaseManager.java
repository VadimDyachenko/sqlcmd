package SQLcmd.model;

import java.sql.*;
import java.util.*;

public class JDBCPostgreDatabaseManager implements DatabaseManager {
    private Connection connection;

//    static {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            try {
//                DriverManager.registerDriver(new org.postgresql.Driver());
//            } catch (SQLException e1) {
//                try {
//                    throw new SQLException("Couldn't register driver in case -", e1);
//                } catch (SQLException e2) {
//                    e2.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    public void connect(String driver, String serverIP, String serverPort, String databaseName, String user, String password) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.getMessage();
        }
        String url =  String.format("%s%s:%s/%s", driver, serverIP, serverPort, databaseName);
        connection = DriverManager.getConnection( url, user, password);
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public Set<String> getAllTableNames() throws SQLException {
        checkConnection();

        Set<String> resultTableNames = new LinkedHashSet<>();

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
    public void createTableRecord(String tableName, DataSet newValue) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement()) {
            String columnName = getNameFormatted(newValue, "%s,");
            String values = getValuesFormatted(newValue, "'%s',");
            statement.executeUpdate(
                    String.format("INSERT INTO public.%s(%s)VALUES (%s)", tableName, columnName, values));
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM public.%s", tableName))
        ) {
            int size = getSize(tableName);

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            DataSet[] result = new DataSetImpl[size];
            int index = 0;
            while (resultSet.next()) {
                DataSet dataSet = new DataSetImpl();
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
        String sql = String.format("UPDATE public.%s SET %s WHERE id = ?", tableName, tableNames);

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
    public void clearCurrentTable(String tableName) throws SQLException {
        checkConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM public.%s", tableName));
        }
    }

    @Override
    public Set<String> getTableColumnNames(String tableName) throws SQLException {
        checkConnection();
        Set<String> result = new LinkedHashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     String.format("SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' " +
                             "and table_name = '%s'", tableName))
        ) {
            while (resultSet.next()) {
                result.add(resultSet.getString("column_name"));
            }
            return result;
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
        ResultSet resultSet = statement.executeQuery(String.format("SELECT count(*) FROM public.%s", tableName));
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
