package sqlcmd;

import org.junit.Before;
import org.junit.Test;
import sqlcmd.DatabaseManager;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vadim on 24.09.2016.
 */
public class DatabaseManagerTest {
    private DatabaseManager manager;
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
        manager.clear("users");
        //when
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("age", 40);
        manager.create(inputData);
        //then
        DataSet[] users = manager.getTableData("users");
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, age]", Arrays.toString(user.getNames()));
        assertEquals("[10, Semen Petrov, 40]", Arrays.toString(user.getValues()));

    }
}
