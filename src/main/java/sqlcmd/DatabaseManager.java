package sqlcmd;

public interface DatabaseManager {

    void connect(String database, String user, String password);

    void disconnect();

    String[] getAllTableNames();

    void create(DataSet users);

    DataSet[] getTableData(String tableName);

    void update(String tableName, int id, DataSet newValue);

    void clear(String database);
}
