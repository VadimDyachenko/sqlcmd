package ua.vadim.sqlcmd;

import ua.vadim.sqlcmd.controller.Controller;
import ua.vadim.sqlcmd.model.DatabaseManager;
import ua.vadim.sqlcmd.model.JDBCPostgreDatabaseManager;
import ua.vadim.sqlcmd.view.Console;
import ua.vadim.sqlcmd.view.View;

import java.util.Properties;

public class SQLcmdMain {
    static Properties properties = new Properties();

    public static void main(String[] args) {
        DatabaseManager manager = new JDBCPostgreDatabaseManager();
        View view = new Console();
        Controller controller = new Controller(manager, view);
        controller.run();
    }
}
