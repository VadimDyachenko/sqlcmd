package ua.com.vadim.SQLcmd.model;

import org.junit.*;
import org.postgresql.util.PSQLException;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PostgresDBManagerTest {
    private ByteArrayOutputStream consoleOutputStream;
    private static DatabaseManager manager;
    private static RunParameters runParameters;
    private static String DATABASE;
    private static String USER_NAME;
    private static String USER_PASSWORD;
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE + " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";
    private static final String WRONG_SERVER_IP = "192.168.1.1";
    private static final String WRONG_SERVER_PORT = "5433";

    @BeforeClass
    public static void beforeAllTestSetUp() {
        runParameters = new PropertiesLoader().getParameters();
        DATABASE = runParameters.getDatabaseName();
        USER_NAME = runParameters.getUserName();
        USER_PASSWORD = runParameters.getPassword();

        manager = new PostgresDBManager(runParameters.getServerIP(), runParameters.getServerPort());
        try {
            manager.connect(DATABASE, USER_NAME, USER_PASSWORD);
            manager.dropDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE);
            manager.disconnect();
            manager.connect(TEST_DATABASE, USER_NAME, USER_PASSWORD);
            manager.createTable(CREATE_TABLE_QUERY);
            manager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void afterAllTestsClear() {
        try {
            manager.connect(DATABASE, USER_NAME, USER_PASSWORD);
            manager.dropDatabase(TEST_DATABASE);
            manager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        consoleOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(consoleOutputStream));
        try {
            manager.connect(TEST_DATABASE, USER_NAME, USER_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUpStream() {
        System.setOut(null);
        runParameters.setTableName(TEST_TABLE);
        try {
            if (!manager.isConnected()) {
                manager.connect(TEST_DATABASE, USER_NAME, USER_PASSWORD);
            }
            manager.clearTable(TEST_TABLE);
            putDataToTestTable();
            manager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void putDataToTestTable() throws SQLException {
        manager.clearTable(TEST_TABLE);
        createRecord(1, "Semen Petrov", "qwert");
        createRecord(2, "Bob Marley", "pass1");
        createRecord(3, "Coca Cola", "pepsithebest");
    }

    private void createRecord(int id, String name, String password) throws SQLException {
        DataSet inputData = new DataSetImpl();
        inputData.put("id", id);
        inputData.put("name", name);
        inputData.put("password", password);
        manager.createTableRecord(TEST_TABLE, inputData);
    }

    @Test
    public void testGetAllTableNames() throws SQLException {
        //given
        Set<String> tableNames = manager.getAllTableNames();
        //when
        printTableNames(tableNames);
        //then
        assertEquals("[" + TEST_TABLE + "]", consoleOutputStream.toString());
    }

    @Test
    public void testGetTableColumnNames() throws SQLException {
        //given
        Set<String> columnNames = manager.getTableColumnNames(TEST_TABLE);
        //when
        printTableNames(columnNames);
        //then
        assertEquals("[id, name, password]", consoleOutputStream.toString());
    }


    @Test
    public void testGetTableData() throws SQLException {
        //given
        runParameters.setTableName(TEST_TABLE);
        runParameters.setTableLevel(true);
        manager.clearTable(TEST_TABLE);
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.createTableRecord(TEST_TABLE, inputData);

        //when
        DataSet[] users = manager.getTableData(TEST_TABLE);
        DataSet user = users[0];

        //then
        assertEquals(1, users.length);
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[10, Semen Petrov, qwert]", user.getValues().toString());
    }

    @Test
    public void testUpdateTableData() throws SQLException {
        //given
        runParameters.setTableName(TEST_TABLE);
        manager.clearTable(TEST_TABLE);
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 10);
        inputData.put("name", "Semen Petrov");
        inputData.put("password", "qwert");
        manager.createTableRecord(TEST_TABLE, inputData);
        DataSet newValue = new DataSetImpl();
        newValue.put("password", "abcde");
        newValue.put("name", "Ivan Ivanov");

        //when
        manager.updateTableRecord(TEST_TABLE, 10, newValue);
        DataSet[] users = manager.getTableData(TEST_TABLE);
        DataSet user = users[0];

        //then
        assertEquals(1, users.length);
        assertEquals("[id, name, password]", user.getNames().toString());
        assertEquals("[10, Ivan Ivanov, abcde]", user.getValues().toString());
    }

    @Test
    public void testClearTableData() throws SQLException {
        //given
        runParameters.setTableName(TEST_TABLE);
        runParameters.setTableLevel(true);

        //when
        manager.clearTable(TEST_TABLE);

        //then
        DataSet[] users = manager.getTableData(TEST_TABLE);
        assertEquals(0, users.length);
    }

    @Test
    public void testIsConnected() throws SQLException {
        //given
        //when
        manager.disconnect();

        //then
        assertFalse(manager.isConnected());
    }

    @Ignore
    @Test (expected = PSQLException.class)
    public void testWithoutConnection() throws SQLException {
        //given
        DatabaseManager manager_test = new PostgresDBManager(WRONG_SERVER_IP, WRONG_SERVER_PORT);
        //when
        manager_test.connect(DATABASE, USER_NAME, USER_PASSWORD);

        //then
        assertFalse(manager.isConnected());
    }

    private void printTableNames(Set<String> tableNames) {
        String availableTables = "[";
        for (String name : tableNames) {
            availableTables += name + ", ";
        }
        availableTables = availableTables.substring(0, availableTables.length() - 2) + "]";
        System.out.print(availableTables);
    }
}