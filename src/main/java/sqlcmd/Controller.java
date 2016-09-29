package sqlcmd;


public class Controller {
    public static void main(String[] args) {
        DatabaseManager manager = new JDBCDatabaseManager();
        manager.connect("sqlcmd", "javauser", "test");
        manager.disconnect();
        manager.disconnect();
    }
}
