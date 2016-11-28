package ua.com.vadim.SQLcmd;

import ua.com.vadim.SQLcmd.controller.Controller;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.View;

import java.util.Locale;

public class SQLcmdMain {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        View console = new Console();
        Controller controller = new Controller(console);
        controller.run();
    }
}
