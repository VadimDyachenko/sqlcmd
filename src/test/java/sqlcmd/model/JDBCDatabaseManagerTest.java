package sqlcmd.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
        manager.setCurrentTableName(TABLE_NAME);

        try {
            manager.clearCurrentTable();
            DataSet inputData1 = new DataSet();
            inputData1.put("id", 1);
            inputData1.put("name", "Semen Petrov");
            inputData1.put("password", "qwert");
            manager.createTableRecord(TABLE_NAME, inputData1);
            DataSet inputData2 = new DataSet();
            inputData2.put("id", 2);
            inputData2.put("name", "Bob Marley");
            inputData2.put("password", "pass1");
            manager.createTableRecord(TABLE_NAME, inputData2);
            DataSet inputData3 = new DataSet();
            inputData3.put("id", 3);
            inputData3.put("name", "Coca Cola");
            inputData3.put("password", "pepsithebest");
            manager.createTableRecord(TABLE_NAME, inputData3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAllTableNames() {

        List<String> tableNames = null;
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        printTableNames(tableNames);
        assertEquals("[users, staff]", consoleOutputStream.toString());
    }

    @Test
    public void getTableColumnNames() {
        //given
        //when
        List<String> columnNames = null;
        try {
            columnNames = manager.getTableColumnNames(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        printTableNames(columnNames);
        assertEquals("[id, name, password]", consoleOutputStream.toString());
    }


    @Test
    public void testGetTableData(){
        //given
        manager.setCurrentTableName(TABLE_NAME);
        manager.changeTableLayer(true);
        try {
            manager.clearCurrentTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //when
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        try {
            manager.createTableRecord(TABLE_NAME, inputData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //then
        DataSet[] users = new DataSet[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Semen Petrov, qwert]", Arrays.toString(user.getValues()));

    }

    @Test
    public void testUpdateTableData() {
        //given
        manager.setCurrentTableName(TABLE_NAME);

        try {
        manager.clearCurrentTable();
        DataSet inputData = new DataSet();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.createTableRecord(TABLE_NAME, inputData);

        //when
        DataSet newValue = new DataSet();
        newValue.put("password", "abcde");
        newValue.put("name", "Bob Marley");
            manager.updateTableRecord(TABLE_NAME, 10, newValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSet[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", Arrays.toString(user.getNames()));
        assertEquals("[10, Bob Marley, abcde]", Arrays.toString(user.getValues()));
    }

    @Test
    public void testClearTableData() {
        //given
        manager.setCurrentTableName(TABLE_NAME);
        manager.changeTableLayer(true);

        //when
        try {
            manager.clearCurrentTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSet[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
