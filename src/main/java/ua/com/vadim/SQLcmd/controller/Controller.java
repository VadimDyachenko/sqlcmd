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
    private RunParameters parameters;
    private LocaleSelector localeSelector;
    private ResourceBundle resource;
    private DatabaseManager manager;

    public Controller(View view) {
        this.view = view;
        parameters = new PropertiesLoader().getParameters();
        localeSelector= new LocaleSelector();
        manager = new PostgresDBManager(parameters.getServerIP(),
                parameters.getServerPort());
    }

    public void run() {
        try {
            setUp();

            view.writeMessage(resource.getString("common.welcome"));
            tryDBConnectWithDefaultParameters();

            while (true) {
                commandExecutor.execute(AvailableCommand.PRINT_CONNECTION_STATUS);
                AvailableCommand command = askCommand();
                commandExecutor.execute(command);
            }
        } catch (ExitException e) {
            try {
                manager.disconnect();
            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                view.writeMessage(resource.getString("common.the.end"));
            }
        }
    }

    private void setUp() throws ExitException {
        localeSetUp();
        resource = ResourceBundle.getBundle("common", new UTF8Control());
        commandExecutor = new CommandExecutor(parameters, manager, view);
    }

    private AvailableCommand askCommand() throws ExitException {
        view.writeMessage(resource.getString("common.choice.operation"));
        while (true) {
            try {
                printMenu();
                Integer numOfChoice = Integer.parseInt(readLine());

                if (parameters.isTableLevel()) {
                    return AvailableCommand.getTableCommand(numOfChoice);
                } else {
                    return AvailableCommand.getMainCommand(numOfChoice);
                }
            } catch (IllegalArgumentException e) {
                view.writeMessage(resource.getString("common.choice.correct"));
            }
        }
    }

    private void printMenu() {
        if (parameters.isTableLevel()) {
            printTableMenu();
        } else {
            printMainMenu();
        }
    }

    private void printMainMenu() {
        view.writeMessage(resource.getString("common.main.menu"));
    }

    private void printTableMenu() {
        view.writeMessage(resource.getString("common.table.menu"));
    }


    private void localeSetUp() {
        try {
            localeSelector.setLocale(parameters.getInterfaceLanguage());
        } catch (UnsupportedLanguageException e) {
            view.writeMessage("Unsupported language parameter in sqlcmd.properties file.");
            view.writeMessage("Exit the program and change it to available variant: " +
                    localeSelector.getSupportedLocale().toString());
            view.writeMessage("Current interface language setting to [en]\n");
            localeSelector.setEnglishLocale();
        }
    }

    private void tryDBConnectWithDefaultParameters() throws ExitException {
        view.writeMessage(resource.getString("common.try.connect.default.parameters"));
        commandExecutor.execute(AvailableCommand.DB_CONNECT);
    }

    private String readLine() throws ExitException {

        String result = view.read();
        if ("exit".equalsIgnoreCase(result)) {
            throw new ExitException();
        }

        return result;
    }
}
