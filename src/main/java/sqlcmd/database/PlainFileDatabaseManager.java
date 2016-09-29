package sqlcmd.database;

/**
 * Created by Vadim on 27.09.2016.
 */
public class PlainFileDatabaseManager implements DatabaseManager {
    //TODO implement methods

    @Override
    public void connect(String fileName, String user, String password) {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public String[] getAllTableNames() {
        return new String[0];
    }

    @Override
    public void create(DataSet users) {

    }

    @Override
    public DataSet[] getTableData(String tableName) {
        return new DataSet[0];
    }

    @Override
    public void update(String tableName, int id, DataSet newValue) {

    }

    @Override
    public void clear(String database) {

    }
}
