package sqlcmd;

import sqlcmd.controller.Controller;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;
import sqlcmd.view.Console;
import sqlcmd.view.View;

public class SQLcmdMain {

    public static void main(String[] args) {
        DatabaseManager manager = new JDBCDatabaseManager();
        View view = new Console();
        Controller controller = new Controller(manager, view);
        controller.run();
    }
}
