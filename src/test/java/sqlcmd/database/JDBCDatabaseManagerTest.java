package sqlcmd.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sqlcmd.database.DataSet;
import sqlcmd.database.DatabaseManager;
import sqlcmd.database.JDBCDatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vadim on 24.09.2016.
 */
public class JDBCDatabaseManagerTest {
    private final ByteArrayOutputStream consoleOutputStream = new ByteArrayOutputStream();
    private DatabaseManager manager;
    private static final String TABLE_NAME ="users";
    @Before
    public void setupManager() {
        manager = new JDBCDatabaseManager();
        try {
            manager.connect("sqlcmd", "javauser", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.setOut(new PrintStream(consoleOutputStream));
    }

    @After
    public void cleanUpStream() {
        System.setOut(null);
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getAllTableNames();
        assertEquals("[users, staff]", Arrays.toString(tableNames));
    }

    @Test
    public void testGetTableData(){
        //given
        manager.clear(TABLE_NAME);
        //when
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.create(TABLE_NAME, inputData);
        //then
        DataSet[] users = manager.getTableData(TABLE_NAME);
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Semen Petrov, qwert]", Arrays.toString(user.getValues()));

    }

    @Test
    public void testUpdateTableData() {
        //given
        manager.clear(TABLE_NAME);
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.create(TABLE_NAME, inputData);

        //when
        DataSet newValue = new DataSet();
        newValue.put("password", "abcde");
        newValue.put("name", "Bob Marley");
        manager.update(TABLE_NAME, 10, newValue);

        //then
        DataSet[] users = manager.getTableData(TABLE_NAME);
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Bob Marley, abcde]", Arrays.toString(user.getValues()));
    }

//    @Test
//    public void disconnectTest1() {
//        manager.disconnect();
//        assertEquals("Connection closed.\n", consoleOutputStream.toString());
//    }
//
//    @Test
//    public void disconnectTest2() {
//        //when
//        manager.disconnect();
//        //than
//        manager.disconnect();
//        assertEquals("Connection closed.\n" +
//                "No any established connections.\n", consoleOutputStream.toString());
//    }
}
