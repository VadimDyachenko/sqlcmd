package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller {

    private View view;
    private CommandExecutor commandExecutor;
    private RunParameters runParameters;
    private LocaleSelector localeSelector;
    private ResourceBundle res;
    private DatabaseManager manager;

    public Controller(View view) {
        this.view = view;
        runParameters = new PropertiesLoader().getParameters();
        localeSelector= new LocaleSelector();

    }

    public void run() {
        try {
            setUp();
            view.writeMessage(res.getString("common.welcome"));
            tryDBconnectWithDefaultParameters();
            while (true) {
                commandExecutor.execute(AvailableCommand.PRINT_CURRENT_CONNECTION_STATUS);
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
        } catch (ExitException e) {
            try {
                manager.disconnect();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            finally {
                view.writeMessage(res.getString("common.the.end"));
            }
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
        localeSetUp();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "common", new UTF8Control());
        manager = new PostgresDBManager(runParameters.getServerIP(),
                runParameters.getServerPort());
        commandExecutor = new CommandExecutor(runParameters, manager, view);
    }

    private void localeSetUp() {
        try {
            localeSelector.setLocale(runParameters.getInterfaceLanguage());
        } catch (UnsupportedLanguageException e) {
            view.writeMessage("Unsupported language parameter in sqlcmd.properties file.");
            view.writeMessage("Exit the program and change it to available variant: " +
                    localeSelector.getSupportedLocale().toString());
            view.writeMessage("Current interface language setting to [en]\n");
            localeSelector.setEnglishLocale();
        }
    }

    private void tryDBconnectWithDefaultParameters() throws ExitException {
        view.writeMessage(res.getString("common.try.connect.default.parameters"));
        commandExecutor.execute(AvailableCommand.DB_CONNECT);
    }
}
