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
    private static RunParameters parameters;
    private static String DATABASE;
    private static String NAME;
    private static String PASSWORD;
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE + " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";
    private static final String WRONG_IP = "192.168.1.1";
    private static final String WRONG_PORT = "5433";

    @BeforeClass
    public static void beforeAllTestSetUp() {
        parameters = new PropertiesLoader().getParameters();
        DATABASE = parameters.getDatabaseName();
        NAME = parameters.getUserName();
        PASSWORD = parameters.getPassword();

        manager = new PostgresDBManager(parameters.getServerIP(), parameters.getServerPort());
        try {
            manager.connect(DATABASE, NAME, PASSWORD);
            manager.dropDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE);
            manager.disconnect();
            manager.connect(TEST_DATABASE, NAME, PASSWORD);
            manager.createTable(CREATE_TABLE_QUERY);
            manager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void afterAllTestsClear() {
        try {
            manager.connect(DATABASE, NAME, PASSWORD);
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
            manager.connect(TEST_DATABASE, NAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUpStream() {
        System.setOut(null);
        parameters.setTableName(TEST_TABLE);
        try {
            if (!manager.isConnected()) {
                manager.connect(TEST_DATABASE, NAME, PASSWORD);
            }
            manager.clearTable(TEST_TABLE);
            manager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        parameters.setTableLevel(true);
        putData();

        //when
        DataSet[] result = manager.getTableData(TEST_TABLE);

        //then
        assertEquals(1, result.length);
        assertEquals("[id, name, password]", result[0].getNames().toString());
        assertEquals("[1, User, abcde]", result[0].getValues().toString());
    }

    @Test
    public void testUpdateTableData() throws SQLException {
        //given
        putData();
        DataSet newValue = new DataSetImpl();
        newValue.put("password", "abcde");
        newValue.put("name", "Ivan Ivanov");

        //when
        manager.updateTableRecord(TEST_TABLE, 1, newValue);
        DataSet[] result = manager.getTableData(TEST_TABLE);

        //then
        assertEquals(1, result.length);
        assertEquals("[id, name, password]", result[0].getNames().toString());
        assertEquals("[1, Ivan Ivanov, abcde]", result[0].getValues().toString());
    }


    @Test
    public void testClearTableData() throws SQLException {
        //given
        parameters.setTableName(TEST_TABLE);
        parameters.setTableLevel(true);

        //when
        manager.clearTable(TEST_TABLE);

        //then
        DataSet[] result = manager.getTableData(TEST_TABLE);
        assertEquals(0, result.length);
    }

    @Test
    public void testIsConnected() throws SQLException {
        //when
        manager.disconnect();

        //then
        assertFalse(manager.isConnected());
    }

    @Test (expected = PSQLException.class)
    public void testWithoutConnection() throws SQLException {
        //given
        DatabaseManager manager_test = new PostgresDBManager(WRONG_IP, WRONG_PORT);

        //when
        manager_test.connect(DATABASE, NAME, PASSWORD);

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

    private void putData() throws SQLException {
        parameters.setTableName(TEST_TABLE);
        manager.clearTable(TEST_TABLE);
        DataSet inputData = new DataSetImpl();
        inputData.put("id", 1);
        inputData.put("name", "User");
        inputData.put("password", "abcde");
        manager.createTableRecord(TEST_TABLE, inputData);
    }
}
