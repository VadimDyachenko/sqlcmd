package sqlcmd.controller;

/**
 * Этот класс предназначен для хранения имени базы данных и имени таблицы,
 * с которой работаем в текущей позииции меню класса Controller
 */

public class ConnectionStatusHelper {
    private String currentDatabaseName = "";
    private String currentTableName = "";
    private boolean tableLevel;

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    public void setTableLevel(boolean tableLevel) {
        this.tableLevel = tableLevel;
    }

    public boolean getTableLevel() {
        return tableLevel;
    }
}
