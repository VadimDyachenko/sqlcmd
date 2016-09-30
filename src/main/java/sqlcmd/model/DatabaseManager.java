package sqlcmd.model;

import java.util.Map;

public interface DatabaseManager {

    void connect(String database, String user, String password) throws Exception;

    void disconnect();

    String[] getAllTableNames();

    void create(String tableName, DataSet data);

    DataSet[] getTableData(String tableName);

    void update(String tableName, int id, DataSet newValue);

    void clear(String database);

    Map<String, Integer> getTableRowLenght(String tableName);
}
