package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesLoader {
    private static final String CONFIG_SQLCMD_PROPERTIES = "configuration/sqlcmd.properties";
    private Properties properties;

    public PropertiesLoader() {
        properties = new Properties();
        File file = new File(CONFIG_SQLCMD_PROPERTIES);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public RunParameters getParameters() throws ExitException {
        return new RunParameters(
                getServerIP(),
                getServerPort(),
                getDatabaseName(),
                getDriver(),
                getUserName(),
                getPassword(),
                getInterfaceLanguage()
        );
    }

    private String getServerIP() {
        return properties.getProperty("database.server.ip");
    }

    private String getServerPort() {
        return properties.getProperty("database.server.port");
    }

    private String getDatabaseName() {
        return properties.getProperty("database.name");
    }

    private String getDriver() {
        return properties.getProperty("database.jdbc.driver");
    }

    private String getUserName() {
        return properties.getProperty("database.user.name");
    }

    private String getPassword() {
        return properties.getProperty("database.user.password");
    }

    private String getInterfaceLanguage() throws ExitException{
        String interfaceLanguage = properties.getProperty("sqlcmd.language");
        try {
            //TODO вынести поддерживаемые языки в отдельный контейнер, исправить проверку поддерживаемых языков
            if (interfaceLanguage.equalsIgnoreCase("en") || interfaceLanguage.equalsIgnoreCase("ru")) {
                return interfaceLanguage.toLowerCase();
            } else throw new UnsupportedLanguageException();
        } catch (UnsupportedLanguageException e) {
            System.out.println(e.getMessage()); //TODO выбросить обработку сообщений выше
            throw new ExitException();
        }
    }
}
