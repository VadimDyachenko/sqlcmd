package sqlcmd;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    private Connection getConnection() {
        return connection;
    }

    public void connect(String database, String user, String password) {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://192.168.1.5:5432/" + database, user, password);
        } catch (SQLException e) {
            System.out.println(String.format("Cant get connection for database: %s user: %s", database, user));
            e.printStackTrace();
            connection = null;
        }

    }

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

    public void create(DataSet users) {
        try {
            Statement statement = connection.createStatement();
            String tableNames = getNameFormated(users, "%s,");
            String values = getValuesFormated(users, "'%s',");

            statement.executeUpdate("INSERT INTO public.users (" + tableNames + ")" + "VALUES (" + values + ")");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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


    public void clear(String database) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM public." + database);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getNameFormated(DataSet newValue, String format) {
        String string = "";
        for (String name : newValue.getNames()) {
            string += String.format(format, name);
        }
        string = string.substring(0, string.length() - 1);
        return string;
    }

    private String getValuesFormated(DataSet input, String format) {
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
