package ua.com.vadim.SQLcmd;

import ua.com.vadim.SQLcmd.controller.Controller;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.View;

public class SQLCmdMain {

    public static void main(String[] args) {
        View console = new Console();
        Controller controller = new Controller(console);
        controller.run();
    }
}
