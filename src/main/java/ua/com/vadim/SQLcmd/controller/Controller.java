package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.View;

import java.util.Locale;
import java.util.ResourceBundle;

public class Controller {

    private View view;
    private CommandExecutor commandExecutor;
    private RunParameters runParameters;
    private ResourceBundle res;

    public void run() {
        try {
            setUp();
            view.writeMessage(res.getString("common.welcome"));
            do {
                commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
            while (true);
        } catch (ExitException e) {
            //NOP
        }
    }

    private AvailableCommand askCommand() {
        view.writeMessage(res.getString("common.choice.operation"));
        while (true) {
            try {
                printMenu();
                Integer numOfChoice = Integer.parseInt(view.readLine());
                if (runParameters.isTableLevel()) return AvailableCommand.getTableCommand(numOfChoice);
                else return AvailableCommand.getMainCommand(numOfChoice);

            } catch (IllegalArgumentException e) {
                view.writeMessage(res.getString("common.choice.correct"));
            }
        }
    }

    private void printMenu() {
        if (runParameters.isTableLevel()) {
            printTableMenu();
        } else {
            printMainMenu();
        }
    }

    private void printMainMenu() {
        view.writeMessage(res.getString("common.main.menu"));
    }

    private void printTableMenu() {
        view.writeMessage(res.getString("common.table.menu"));
    }

    private void setUp() {
        runParameters = new PropertiesLoader().getParameters();
        setLocale();
        view = new Console();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "common");
        DatabaseManager manager = new PostgresDBManager(runParameters.getDriver(),
                runParameters.getServerIP(),
                runParameters.getServerPort());
        commandExecutor = new CommandExecutor(runParameters, manager, view);
        tryConnectToDefaultDatabase();
    }

    private void tryConnectToDefaultDatabase() {
    }

    private void setLocale() {
        if (runParameters.getInterfaceLanguage().equals("ru")) { //TODO вынести поддерживаемые локали в контейнер и делать проверки по содержанию ключа в контейнере
            Locale ruLocale = new Locale("ru", "RU");
            Locale.setDefault(ruLocale);
        } else {
            Locale.setDefault(Locale.ENGLISH);
        }
    }
}
