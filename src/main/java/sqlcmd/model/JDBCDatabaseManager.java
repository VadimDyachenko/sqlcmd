package sqlcmd.model;

import java.sql.*;
import java.util.HashMap;

public class JDBCDatabaseManager implements DatabaseManager {
    private Connection connection;

    @Override
    public void connect(String database, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://192.168.1.5:5432/" + database, user, password);
    }

    @Override
    public void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getAllTableNames() {
        String[] resultTableNames;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public'");
            resultSet.next();
            resultTableNames = new String[resultSet.getInt("count")];
            resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'");
            int index = 0;
            while (resultSet.next()) {
                resultTableNames[index++] = resultSet.getString("table_name");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
        return resultTableNames;
    }

    @Override
    public void create(String tableName, DataSet users) {
        try {
            Statement statement = connection.createStatement();
            String columnName = getNameFormatted(users, "%s,");
            String values = getValuesFormatted(users, "'%s',");
            statement.executeUpdate("INSERT INTO public." + tableName + "(" + columnName + ")" + "VALUES (" + values + ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public DataSet[] getTableData(String tableName) {
        try {
            int size = getSize(tableName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM public." + tableName);
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
            resultSet.close();
            statement.close();
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            return new DataSet[0];
        }
    }

    @Override
    public void update(String tableName, int id, DataSet newValue) {
        try {
            String tableNames = getNameFormatted(newValue, "%s = ?,");
            String sql = "UPDATE public." + tableName + " SET " + tableNames + " WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            int index = 1;
            for (Object value : newValue.getValues()) {
                ps.setObject(index, value);
                index++;
            }
            ps.setObject(index, id);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear(String database) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM public." + database);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает HashМap(Название колонки, максимальная длина поля колонки)
     * @param tableName
     * @return result
     */
    public HashMap<String, Integer> getTableRowLenght(String tableName) {
        try {
            Statement statement = connection.createStatement();
            HashMap<String, Integer> result = new HashMap<>();
            ResultSet resultTableSet = statement.executeQuery(
                    "SELECT column_name, data_type " +
                    "FROM information_schema.columns " +
                    "WHERE table_schema = 'public' and table_name = '" + tableName + "'"
            );
//            select table_schema, table_name, column_name, data_type
//            from information_schema.columns
//            where table_schema = 'tt' and table_name = 't';
//            table_schema | table_name | column_name | data_type
//           --------------+------------+-------------+-----------
//            tt           | t          | i           | integer
//            tt           | t          | n           | numeric

            while (resultTableSet.next()) {
                String key = resultTableSet.getString("column_name");
                String dataType = resultTableSet.getString("data_type");
                Integer value = getRowLength(tableName, key, dataType);
                result.put(key, value);
            }

            resultTableSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод возвращает максимальную длину поля колонки
     * @param tableName
     * @param columnName
     * @param dataType
     * @return Integer
     */
    private Integer getRowLength(String tableName, String columnName, String dataType) {
        try {
            ResultSet resultSet;
            Statement statement = connection.createStatement();
            String sqlQuery = "";
            if (dataType.equals("integer")) {
                sqlQuery = "SELECT max(" + columnName + ") FROM " + tableName;
            } else if (dataType.equals("character varying")) {
                sqlQuery = "SELECT max(char_length(" + columnName + ")) AS Max_Length_String FROM " + tableName;
            } else {
                return -1;  //No compatible data_type
            }
            resultSet = statement.executeQuery(sqlQuery);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
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
