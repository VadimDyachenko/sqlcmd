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
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
            while (true);
        } catch (ExitException e) {
            //NOP
        }
    }

    private AvailableCommand askCommand() throws ExitException {
        commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);
        view.writeMessage(res.getString("common.choise.operation"));
        while (true) {
            try {
                printMenu();
                String choice = view.readLine();
                Integer numOfChoice = Integer.parseInt(choice);
                return runParameters.isTableLevel()
                        ? AvailableCommand.getTableCommand(numOfChoice)
                        : AvailableCommand.getMainCommand(numOfChoice);

            } catch (IllegalArgumentException e) {
                view.writeMessage(res.getString("common.choise.correct"));
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
        view = new Console();
        runParameters = new PropertiesLoader().getParameters();
        setLocale();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "common");
        DatabaseManager manager = new PostgresDBManager(runParameters.getDriver(),
                runParameters.getServerIP(),
                runParameters.getServerPort());
        commandExecutor = new CommandExecutor(runParameters, manager, view);
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
