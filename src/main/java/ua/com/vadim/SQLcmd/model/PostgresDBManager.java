package ua.com.vadim.SQLcmd.model;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class PostgresDBManager implements DatabaseManager {
    private final String url;
    private Connection connection;

    public PostgresDBManager(String serverIP, String serverPort) {
        this.url = String.format("jdbc:postgresql://%s:%s/", serverIP, serverPort);
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(String databaseName, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url + databaseName, user, password);
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public void createDatabase(String database) throws SQLException{
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(String.format("CREATE DATABASE %s;", database));
        }
    }

    @Override
    public void dropDatabase(String database) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(String.format("DROP DATABASE IF EXISTS %s;", database));
        }
    }

    @Override
    public void createTable(String query) throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(String.format("CREATE TABLE public.%s;", query));
        }
    }

    @Override
    public Set<String> getAllTableNames() throws SQLException {
        Set<String> result = new LinkedHashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT table_name FROM information_schema.tables " +
                             "WHERE table_schema='public' AND table_type='BASE TABLE'")
        ) {
            while (resultSet.next()) {
                result.add(resultSet.getString("table_name"));
            }
            return result;
        }
    }

    @Override
    public void createTableRecord(String table, DataSet newValue) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String column = getNameFormatted(newValue, "%s,");
            String values = getValuesFormatted(newValue);
            statement.executeUpdate(
                    String.format("INSERT INTO public.%s(%s)VALUES (%s)", table, column, values));
        }
    }

    @Override
    public DataSet[] getTableData(String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(String.format("SELECT * FROM public.%s", table))
        ) {
            int size = getSize(table);

            ResultSetMetaData metaData = resultSet.getMetaData();
            DataSet[] result = new DataSetImpl[size];
            int index = 0;
            while (resultSet.next()) {
                DataSet dataSet = new DataSetImpl();
                result[index++] = dataSet;
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    dataSet.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
                }
            }
            return result;
        }
    }

    @Override
    public void updateTableRecord(String table, int id, DataSet newValue) throws SQLException {
        String names = getNameFormatted(newValue, "%s = ?,");
        String sql = String.format("UPDATE public.%s SET %s WHERE id = ?", table, names);

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
    public void clearTable(String table) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("DELETE FROM public.%s", table));
        }
    }

    @Override
    public Set<String> getTableColumnNames(String table) throws SQLException {
        Set<String> result = new LinkedHashSet<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     String.format("SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' " +
                             "and table_name = '%s'", table))
        ) {
            while (resultSet.next()) {
                result.add(resultSet.getString("column_name"));
            }
            return result;
        }
    }

    @Override
    public boolean isConnected() {
        return (connection != null);
    }

    private String getNameFormatted(DataSet newValue, String format) {
        String result = "";
        for (String name : newValue.getNames()) {
            result += String.format(format, name);
        }
        result = result.substring(0, result.length() - 1);
        return result;
    }

    private String getValuesFormatted(DataSet input) {
        String values = "";
        for (Object value : input.getValues()) {
            values += String.format("'%s',", value);
        }
        values = values.substring(0, values.length() - 1);
        return values;
    }

    private int getSize(String table) throws SQLException {

        try (Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(String.format("SELECT count(*) FROM public.%s", table));
        ) {
            resultSet.next();
            return resultSet.getInt("count");
        }
    }
}
