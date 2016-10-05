package sqlcmd.model;

import java.util.List;

public interface DatabaseManager {

    boolean isTableLayer();

    void changeTableLayer(boolean tableLayer);

    String getCurrentDatabaseName();

    String getCurrentTableName();

    void setCurrentDatabaseName(String currentDatabaseName);

    void setCurrentTableName(String currentTableName);

    void connect(String database, String user, String password) throws Exception;

    void disconnect();

    List<String> getAllTableNames();

    void create(String tableName, DataSet data);

    DataSet[] getTableData(String tableName);

    void update(String tableName, int id, DataSet newValue);

    void clearCurrentTable();

    List<String> getTableColumnNames(String tableName);

    boolean isConnected();
}
