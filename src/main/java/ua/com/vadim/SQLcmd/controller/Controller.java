package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.util.ResourceBundle;

public class Controller {

    private View view;
    private CommandExecutor commandExecutor;
    private RunParameters runParameters;
    private ResourceBundle res;
    private LocaleSelector localeSelector;

    public Controller(View view) {
        this.view = view;
    }

    public void run() {
        try {
            setUp();
            view.writeMessage(res.getString("common.welcome"));
            tryDBconnectWhithDefaultParameters();
            do {
                commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
            while (true);
        } catch (ExitException e) {
            view.writeMessage(res.getString("common.the.end"));
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

    private void setUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        localeSelector = new LocaleSelector(runParameters, view);
        localeSelector.setDefaultLocale();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "common", new UTF8Control());
        DatabaseManager manager = new PostgresDBManager(runParameters.getDriver(),
                runParameters.getServerIP(),
                runParameters.getServerPort());
        commandExecutor = new CommandExecutor(runParameters, manager, view);
    }

    private void tryDBconnectWhithDefaultParameters() {
        view.writeMessage(res.getString("common.try.connect.default.parameters"));
        commandExecutor.execute(AvailableCommand.DB_CONNECT);
    }
}
