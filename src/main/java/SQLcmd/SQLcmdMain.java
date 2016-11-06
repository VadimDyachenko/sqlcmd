package SQLcmd;

import SQLcmd.controller.Controller;

import java.util.Properties;

public class SQLcmdMain {
    static Properties properties = new Properties();

    public static void main(String[] args) {
//        DatabaseManager manager = new PostgreDatabaseManager();
//        View view = new Console();
        Controller controller = new Controller();
        controller.run();
    }
}
