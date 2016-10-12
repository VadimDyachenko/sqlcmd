package sqlcmd.model;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseManager {

    void connect(String database, String user, String password) throws SQLException;

    void disconnect() throws SQLException;

    List<String> getAllTableNames() throws SQLException;

    DataSet[] getTableData(String tableName) throws SQLException;

    void createTableRecord(String tableName, DataSet data) throws SQLException;

    void updateTableRecord(String tableName, int id, DataSet newValue) throws SQLException;

    void clearCurrentTable(String tableName) throws SQLException;

    List<String> getTableColumnNames(String tableName) throws SQLException;

    boolean isConnected();
}
