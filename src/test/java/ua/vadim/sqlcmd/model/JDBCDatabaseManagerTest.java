package ua.vadim.sqlcmd.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Set;

import static org.junit.Assert.*;

public class JDBCDatabaseManagerTest {
    private final ByteArrayOutputStream consoleOutputStream = new ByteArrayOutputStream();
    private DatabaseManager manager;
    private static final String TABLE_NAME ="users";
    private ConnectionStatusHelper connectionStatusHelper;

    @Before
    public void setUp() {
        manager = new JDBCPostgreDatabaseManager();
        connectionStatusHelper = new ConnectionStatusHelper();
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
        connectionStatusHelper.setCurrentTableName(TABLE_NAME);

        try {
            manager.clearCurrentTable(TABLE_NAME);
            DataSet inputData1 = new DataSetImpl();
            inputData1.put("id", 1);
            inputData1.put("name", "Semen Petrov");
            inputData1.put("password", "qwert");
            manager.createTableRecord(TABLE_NAME, inputData1);
            DataSet inputData2 = new DataSetImpl();
            inputData2.put("id", 2);
            inputData2.put("name", "Bob Marley");
            inputData2.put("password", "pass1");
            manager.createTableRecord(TABLE_NAME, inputData2);
            DataSet inputData3 = new DataSetImpl();
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

        Set<String> tableNames = null;
        try {
            tableNames = manager.getAllTableNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        printTableNames(tableNames);
        assertEquals("[users, staff]", consoleOutputStream.toString());
    }

    @Test
    public void testGetTableColumnNames() {
        //given
        //when
        Set<String> columnNames = null;
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
        connectionStatusHelper .setCurrentTableName(TABLE_NAME);
        connectionStatusHelper .setTableLevel(true);
        try {
            manager.clearCurrentTable(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //when
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        try {
            manager.createTableRecord(TABLE_NAME, inputData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[10, Semen Petrov, qwert]", user.getValues().toString());
    }

    @Test
    public void testUpdateTableData() {
        //given
        connectionStatusHelper .setCurrentTableName(TABLE_NAME);

        try {
        manager.clearCurrentTable(TABLE_NAME);
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.createTableRecord(TABLE_NAME, inputData);

        //when
        DataSet newValue = new DataSetImpl();
        newValue.put("password", "abcde");
        newValue.put("name", "Bob Marley");
            manager.updateTableRecord(TABLE_NAME, 10, newValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertEquals(1, users.length);

        DataSet user = users[0];
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[10, Bob Marley, abcde]", user.getValues().toString());
    }

    @Test
    public void testClearTableData() {
        //given
        connectionStatusHelper .setCurrentTableName(TABLE_NAME);
        connectionStatusHelper .setTableLevel(true);

        //when
        try {
            manager.clearCurrentTable(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TABLE_NAME);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertEquals(0, users.length);
    }

    private void printTableNames(Set<String> tableNames)  {

        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";

        System.out.print(availableTables);
    }
}
