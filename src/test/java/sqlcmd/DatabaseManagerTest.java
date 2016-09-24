package sqlcmd;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vadim on 24.09.2016.
 */
public class DatabaseManagerTest {
    private DatabaseManager manager;
    private String tableName ="users";
    @Before
    public void setupManager() {
        manager = new DatabaseManager();
        manager.connect("sqlcmd", "javauser", "test");
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getAllTableNames();
        assertEquals("[users, staff]", Arrays.toString(tableNames));
        System.out.println("");
    }

    @Test
    public void testGetTableData(){
        //given
        manager.clear(tableName);
        //when
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.create(inputData);
        //then
        DataSet[] users = manager.getTableData(tableName);
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Semen Petrov, qwert]", Arrays.toString(user.getValues()));

    }

    @Test
    public void testUpdateTableData() {
        //given
        manager.clear(tableName);
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.create(inputData);

        //when
        DataSet newValue = new DataSet();
        newValue.put("password", "abcde");
        newValue.put("name", "Bob Marley");
        manager.update(tableName, 10, newValue);

        //then
        DataSet[] users = manager.getTableData(tableName);
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Bob Marley, abcde]", Arrays.toString(user.getValues()));
    }
}
