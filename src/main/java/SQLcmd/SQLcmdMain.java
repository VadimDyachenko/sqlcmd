package SQLcmd;

import SQLcmd.controller.Controller;
import SQLcmd.model.DatabaseManager;
import SQLcmd.model.JDBCPostgreDatabaseManager;
import SQLcmd.view.Console;

import SQLcmd.view.View;

import java.util.Properties;

public class SQLcmdMain {
    static Properties properties = new Properties();

    public static void main(String[] args) {
//        DatabaseManager manager = new JDBCPostgreDatabaseManager();
//        View view = new Console();
        Controller controller = new Controller();
        controller.run();
    }
}
