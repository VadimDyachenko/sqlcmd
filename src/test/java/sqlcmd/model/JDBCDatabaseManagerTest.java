package sqlcmd.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        manager.clear(TABLE_NAME);
        DataSet inputData1 = new DataSet();
        inputData1.put("id", 1);
        inputData1.put("name", "Semen Petrov");
        inputData1.put("password", "qwert");
        manager.create(TABLE_NAME, inputData1);
        DataSet inputData2 = new DataSet();
        inputData2.put("id", 2);
        inputData2.put("name", "Bob Marley");
        inputData2.put("password", "pass1");
        manager.create(TABLE_NAME, inputData2);
        DataSet inputData3 = new DataSet();
        inputData3.put("id", 3);
        inputData3.put("name", "Coca Cola");
        inputData3.put("password", "pepsithebest");
        manager.create(TABLE_NAME, inputData3);
    }

    @Test
    public void testGetAllTableNames() {
        List<String> tableNames = manager.getAllTableNames();
        printTableNames(tableNames);
        assertEquals("[users, staff]", consoleOutputStream.toString());
    }

    @Test
    public void getTableColumnNames() {
        //given
        //when
        List<String> columnNames = manager.getTableColumnNames(TABLE_NAME);
        printTableNames(columnNames);
        assertEquals("[id, name, password]", consoleOutputStream.toString());
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

    @Test
    public void testClearTableData() {
        //given
        //when
        manager.clear(TABLE_NAME);
        //then
        DataSet[] users = manager.getTableData(TABLE_NAME);
        assertEquals(0, users.length);
    }

    private void printTableNames(List<String> tableNames)  {

        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";

        System.out.print(availableTables);
    }
}
