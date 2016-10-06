package sqlcmd.model;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseManager {

    boolean isTableLayer();

    void changeTableLayer(boolean tableLayer);

    String getCurrentDatabaseName();

    String getCurrentTableName();

    void setCurrentDatabaseName(String currentDatabaseName);

    void setCurrentTableName(String currentTableName);

    void connect(String database, String user, String password) throws SQLException;

    void disconnect() throws SQLException;

    List<String> getAllTableNames() throws SQLException;

    DataSet[] getTableData(String tableName) throws SQLException;

    void createTableRecord(String tableName, DataSet data) throws SQLException;

    void updateTableRecord(String tableName, int id, DataSet newValue) throws SQLException;

    void clearCurrentTable() throws SQLException;

    List<String> getTableColumnNames(String tableName) throws SQLException;

    boolean isConnected();
}
