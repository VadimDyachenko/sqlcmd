package ua.com.vadim.SQLcmd.integration;

import org.junit.*;
import ua.com.vadim.SQLcmd.SQLCmdMain;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private ConfigurableInputStream consoleInputStream;
    private ByteArrayOutputStream consoleOutputStream;

    private static DatabaseManager manager;
    private static String ip;
    private static String port;
    private static String database;
    private static String user;
    private static String password;
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_DATABASE_EMPTY = "test_db_empty";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE +
            " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";


    @BeforeClass
    public static void beforeAllTestSetUp() {
        Locale.setDefault(Locale.ENGLISH);
        setParameters();

        manager = new PostgresDBManager(ip, port);
        try {
            manager.connect(database, user, password);
            manager.dropDatabase(TEST_DATABASE);
            manager.dropDatabase(TEST_DATABASE_EMPTY);
            manager.createDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE_EMPTY);
            manager.disconnect();
            manager.connect(TEST_DATABASE, user, password);
            manager.createTable(CREATE_TABLE_QUERY);
            manager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setParameters() {
        RunParameters parameters = new PropertiesLoader().getParameters();
        ip = parameters.getServerIP();
        port = parameters.getServerPort();
        database = parameters.getDatabaseName();
        user = parameters.getUserName();
        password = parameters.getPassword();
    }

    @AfterClass
    public static void afterAllTestsClear() {
        try {
            manager.connect(database, user, password);
            manager.dropDatabase(TEST_DATABASE);
            manager.dropDatabase(TEST_DATABASE_EMPTY);
            manager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        consoleInputStream = new ConfigurableInputStream();
        consoleOutputStream = new ByteArrayOutputStream();
        System.setIn(consoleInputStream);
        System.setOut(new PrintStream(consoleOutputStream));
        try {
            manager.connect(TEST_DATABASE, user, password);
            manager.clearTable(TEST_TABLE);
            createRecord(1, "Semen Petrov", "qwert");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createRecord(int id, String name, String password) throws SQLException {
        DataSet inputData = new DataSetImpl();
        inputData.put("id", id);
        inputData.put("name", name);
        inputData.put("password", password);
        manager.createTableRecord(TEST_TABLE, inputData);
    }

    @After
    public void endTest() {
        try {
            manager.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTerminatedExit() {
        //given
        consoleInputStream.addLine("exit");
        String expected = getWelcome() + "Thank you for using SQLCmd. Good luck!\n";

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }


    @Test
    public void testConnectDatabase() {
        //given
        consoleInputStream.addLine("1"); // 1 - Connect to Database (Main menu)
        consoleInputStream.addLine(TEST_DATABASE);
        consoleInputStream.addLine(user);
        consoleInputStream.addLine(password);
        consoleInputStream.addLine("4"); // 4 - Exit (Main menu)
        consoleInputStream.addLine("y");

        String expected = getWelcome() + getLogin() + "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Do you really want to exit? <y/n>\n" +
                "Thank you for using SQLCmd. Good luck!\n";

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }


    @Test
    public void testListAllTableNames() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("2"); // 2 - List Table Names (Main Menu)
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. \n\n" +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Thank you for using SQLCmd. Good luck!\n";

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }

    @Test
    public void testSelectTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                getExit();

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }


    @Test
    public void testPrintTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("1"); // 1 - Print Table Data
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                "Please choose a command desired:\n" +
                "1 - Print table data\n" +
                "2 - Create table record\n" +
                "3 - Update table record\n" +
                "4 - Clear table\n" +
                "5 - Return to previous menu\n" +
                "|id|name        |password|\n" +
                "+--+------------+--------+\n" +
                "|1 |Semen Petrov|qwert   |\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                getExit();

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }


    @Test
    public void testCreateRecord() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("2"); // 2 - Create table record
        consoleInputStream.addLine("id|8|name|ABC|password|DEF");
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                "Please choose a command desired:\n" +
                "1 - Print table data\n" +
                "2 - Create table record\n" +
                "3 - Update table record\n" +
                "4 - Clear table\n" +
                "5 - Return to previous menu\n" +
                "Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n" +
                "Available column name: [id, name, password]\n" +
                "Record {id:8, name:ABC, password:DEF} was create successful.\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                getExit();

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }

    @Test
    public void testUpdateRecord() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("3"); // 3 - Update table record
        consoleInputStream.addLine("id|1|name|Serj|password|pass");
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                "Please choose a command desired:\n" +
                "1 - Print table data\n" +
                "2 - Create table record\n" +
                "3 - Update table record\n" +
                "4 - Clear table\n" +
                "5 - Return to previous menu\n" +
                "Enter data to update table record.\n" +
                "Input format: id|Value_id|ColumnName1|Value1| ... |ColumnNameN|ValueN\n" +
                "Available column name: [id, name, password]\n" +
                "Record {name:Serj, password:pass} was update successful.\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                getExit();

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }

    @Test
    public void testClearTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("4"); // 4 - Clear Table
        consoleInputStream.addLine("y");
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[test_table_users]\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                "Please choose a command desired:\n" +
                "1 - Print table data\n" +
                "2 - Create table record\n" +
                "3 - Update table record\n" +
                "4 - Clear table\n" +
                "5 - Return to previous menu\n" +
                "Do you really want to clear table <test_table_users>? <y/n>\n" +
                "Table clear successful.\n\n" +
                "Connected to database: <test_db_sqlcmd>. Selected table: <test_table_users>.\n\n" +
                getExit();

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }

    @Test
    public void testUnsupportedChoiceInMainMenu() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("0"); // 0 - Unsupported choice
        consoleInputStream.addLine("exit");

        String expected = getWelcome() + getLogin() +
                "Please choose a command desired:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Please choice correct number:\n" +
                "1 - Connect to another database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Thank you for using SQLCmd. Good luck!\n";

        //when
        SQLCmdMain.main(new String[0]);

        //then
        assertEquals(expected, getData());
    }

    private String getWelcome() {
        return "Welcome to SQLCmd!\n" +
                    "Connection to database with default parameter...\n" +
                    "Connection successful!\n\n" +
                    "Connected to database: <sqlcmd>. \n\n" +
                    "Please choose a command desired:\n" +
                    "1 - Connect to another database\n" +
                    "2 - List all table names\n" +
                    "3 - Select table to work\n" +
                    "4 - Exit\n";
    }

    private String getLogin() {
        return "Please, enter new connection parameters or type <exit> to exit programm.\n" +
                "Enter database name:\n" +
                "Enter you login:\n" +
                "Enter you password:\n\n" +
                "Connected to database: <test_db_sqlcmd>. \n\n";
    }

    private String getExit() {
        return "Please choose a command desired:\n" +
                    "1 - Print table data\n" +
                    "2 - Create table record\n" +
                    "3 - Update table record\n" +
                    "4 - Clear table\n" +
                    "5 - Return to previous menu\n" +
                    "Thank you for using SQLCmd. Good luck!\n";
    }

    private void setStartupAnswers() {
        consoleInputStream.addLine("1"); // 1 - Connect to Database (Main menu)
        consoleInputStream.addLine(TEST_DATABASE);
        consoleInputStream.addLine(user);
        consoleInputStream.addLine(password);
    }

    private String getData() {
        try {
            String result = new String(consoleOutputStream.toByteArray(), "UTF-8");
            consoleOutputStream.reset();
            return result.replaceAll(System.lineSeparator(), "\n");
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}
