package sqlcmd;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;

    private Connection getConnection(){
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

    public String[] getAllTableNames(){
        String[] result;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM information_schema.tables WHERE table_schema = 'public'");
            int index = 0;
            resultSet.next();
            result = new String[resultSet.getInt(1)];
            resultSet = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_type='BASE TABLE'");
            while (resultSet.next()) {
                result[index++] = resultSet.getString("table_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return  new String[0];
        }
        return  result;
    }
}
