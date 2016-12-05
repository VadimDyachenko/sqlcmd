package ua.com.vadim.SQLcmd.controller;

public class RunParameters {
    private String serverIP;
    private String serverPort;
    private String databaseName;
    private String userName;
    private String password;
    private String tableName = "";
    private boolean tableLevel = false;
    private final String LANGUAGE_RESOURCES_PATH = "ua.com.vadim.SQLcmd.controller.resources.interface.";
    private String interfaceLanguage;

    public RunParameters(String serverIP, String serverPort,
                         String databaseName, String userName,
                         String password, String interfaceLanguage) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
        this.interfaceLanguage = interfaceLanguage;
    }

    public String getServerIP() {
        return serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setTableLevel(boolean tableLevel) {
        this.tableLevel = tableLevel;
    }

    public boolean isTableLevel() {
        return tableLevel;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getLanguageResourcePath() {
        return LANGUAGE_RESOURCES_PATH;
    }

    public String getInterfaceLanguage() {
        return interfaceLanguage;
    }
}
