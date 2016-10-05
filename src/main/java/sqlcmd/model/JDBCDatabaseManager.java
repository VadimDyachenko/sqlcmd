package sqlcmd.model;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JDBCDatabaseManager implements DatabaseManager {
    private Connection connection;
    private String currentDatabaseName;
    private String currentTableName;
    private boolean tableLayer = false;

    @Override
    public boolean isTableLayer() {
        return tableLayer;
    }

    @Override
    public void changeTableLayer(boolean tableLayer) {
        this.tableLayer = tableLayer;
    }

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    @Override
    public void connect(String databaseName, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://192.168.1.5:5432/" + databaseName, user, password);
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getAllTableNames() {
        List<String> resultTableNames = new LinkedList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'");
        ) {
            while (resultSet.next()) {
                resultTableNames.add(resultSet.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultTableNames;
    }

    @Override
    public void create(String tableName, DataSet users) {
        try (Statement statement = connection.createStatement()) {
            String columnName = getNameFormatted(users, "%s,");
            String values = getValuesFormatted(users, "'%s',");
            statement.executeUpdate("INSERT INTO public." + tableName + "(" + columnName + ")" + "VALUES (" + values + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) {
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
        } catch (SQLException e) {
            e.printStackTrace();
            return new DataSet[0];
        }
    }

    @Override
    public void update(String tableName, int id, DataSet newValue) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCurrentTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM public." + currentTableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getTableColumnNames(String tableName) {
        List<String> resultList = new LinkedList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT column_name FROM information_schema.columns " +
                             "WHERE table_schema = 'public' and table_name = '" + tableName + "'")
        ) {
            while (resultSet.next()) {
                resultList.add(resultSet.getString("column_name"));
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
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
}
