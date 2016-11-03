package SQLcmd.model;

import SQLcmd.controller.PropertiesLoader;
import SQLcmd.controller.RunParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Set;

import static org.junit.Assert.*;

public class JDBCDatabaseManagerTest {
    private final ByteArrayOutputStream consoleOutputStream = new ByteArrayOutputStream();
    private static DatabaseManager manager;
    private static RunParameters runParameters;
    private static final String TEST_TABLE = "testTableUsers";
    private static final String TEST_DATABASE = "testDBsqlcmd2016";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE + " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25)";

    @BeforeClass
    public static void beforeClassSetUp(){
        runParameters = new PropertiesLoader().getParameters();
        manager = new JDBCPostgreDatabaseManager();
        try {
            manager.connect(runParameters.getDriver(),
                            runParameters.getServerIP(),
                            runParameters.getServerPort(),
                            "",
                            runParameters.getUserName(),
                            runParameters.getPassword());
            manager.dropDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE);
            manager.disconnect();
            manager.connect(runParameters.getDriver(),
                            runParameters.getServerIP(),
                            runParameters.getServerPort(),
                            TEST_DATABASE,
                            runParameters.getUserName(),
                            runParameters.getPassword());
            manager.createTable(CREATE_TABLE_QUERY);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Before
    public void setUp() {
//        manager = new JDBCPostgreDatabaseManager();
//        runParameters = new PropertiesLoader().getParameters();
        try {
            manager.connect(runParameters.getDriver(),
                            runParameters.getServerIP(),
                            runParameters.getServerPort(),
                            runParameters.getDatabaseName(),
                            runParameters.getUserName(),
                            runParameters.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            System.setOut(new PrintStream(consoleOutputStream));
        }
    }

    @After
    public void cleanUpStream() {
        System.setOut(null);
        runParameters.setTableName(TEST_TABLE);

        try {
            manager.clearCurrentTable(TEST_TABLE);
            DataSet inputData1 = new DataSetImpl();
            inputData1.put("id", 1);
            inputData1.put("name", "Semen Petrov");
            inputData1.put("password", "qwert");
            manager.createTableRecord(TEST_TABLE, inputData1);
            DataSet inputData2 = new DataSetImpl();
            inputData2.put("id", 2);
            inputData2.put("name", "Bob Marley");
            inputData2.put("password", "pass1");
            manager.createTableRecord(TEST_TABLE, inputData2);
            DataSet inputData3 = new DataSetImpl();
            inputData3.put("id", 3);
            inputData3.put("name", "Coca Cola");
            inputData3.put("password", "pepsithebest");
            manager.createTableRecord(TEST_TABLE, inputData3);
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
            columnNames = manager.getTableColumnNames(TEST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        printTableNames(columnNames);
        assertEquals("[id, name, password]", consoleOutputStream.toString());
    }


    @Test
    public void testGetTableData(){
        //given
        runParameters.setTableName(TEST_TABLE);
        runParameters.setTableLevel(true);
        try {
            manager.clearCurrentTable(TEST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //when
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        try {
            manager.createTableRecord(TEST_TABLE, inputData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TEST_TABLE);
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
        runParameters.setTableName(TEST_TABLE);

        try {
        manager.clearCurrentTable(TEST_TABLE);
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.createTableRecord(TEST_TABLE, inputData);

        //when
        DataSet newValue = new DataSetImpl();
        newValue.put("password", "abcde");
        newValue.put("name", "Bob Marley");
            manager.updateTableRecord(TEST_TABLE, 10, newValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TEST_TABLE);
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
        runParameters.setTableName(TEST_TABLE);
        runParameters.setTableLevel(true);

        //when
        try {
            manager.clearCurrentTable(TEST_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //then
        DataSet[] users = new DataSetImpl[0];
        try {
            users = manager.getTableData(TEST_TABLE);
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
