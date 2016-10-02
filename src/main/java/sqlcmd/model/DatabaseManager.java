package sqlcmd.model;

import java.util.List;

public interface DatabaseManager {

    void connect(String database, String user, String password) throws Exception;

    void disconnect();

    List<String> getAllTableNames();

    void create(String tableName, DataSet data);

    DataSet[] getTableData(String tableName);

    void update(String tableName, int id, DataSet newValue);

    void clear(String database);

    boolean isConnected();
}
