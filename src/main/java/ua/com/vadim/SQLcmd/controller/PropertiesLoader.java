package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.ExitException;


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

    private String getUserName() {
        return properties.getProperty("database.user.name");
    }

    private String getPassword() {
        return properties.getProperty("database.user.password");
    }

    private String getInterfaceLanguage() {
        return properties.getProperty("sqlcmd.language").toLowerCase();
    }
}
