package ua.com.vadim.SQLcmd.integration;

import org.junit.*;
import ua.com.vadim.SQLcmd.controller.LocaleSelector;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.SQLCmdMain;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static RunParameters runParameters;
    private static DatabaseManager manager;
    private static View view;
    private ConfigurableInputStream consoleInputStream;
    private ByteArrayOutputStream consoleOutputStream;
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_DATABASE_EMPTY = "test_db_empty";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE + " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";
    private static final String USER_NAME = "javauser";
    private static final String USER_PASSWORD = "test";
    private static String MAIN_MENU = "common.main.menu";
    private static String TABLE_MENU = "common.table.men";

    private static ResourceBundle res_common;
    private static ResourceBundle res_exit;
    private static ResourceBundle res_connectionStatus;
    private static ResourceBundle res_DBConnect;


    @BeforeClass
    public static void beforeAllTestSetUp() {
        runParameters = new PropertiesLoader().getParameters();
        manager = new PostgresDBManager(runParameters.getServerIP(), runParameters.getServerPort());
        view = new Console();
        localeSetUp();
        resourceSetUp();
        try {
            manager.connect(runParameters.getDatabaseName(), runParameters.getUserName(), runParameters.getPassword());
            manager.dropDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE);
            manager.disconnect();
            manager.connect(TEST_DATABASE, runParameters.getUserName(), runParameters.getPassword());
            manager.createTable(CREATE_TABLE_QUERY);
            manager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void localeSetUp() {
        LocaleSelector localeSelector = new LocaleSelector();
        try {
            localeSelector.setLocale(runParameters.getInterfaceLanguage());
        } catch (UnsupportedLanguageException e) {
            view.writeMessage("Unsupported language parameter in sqlcmd.properties file.");
            view.writeMessage("Current interface language setting to [en]\n");
            localeSelector.setEnglishLocale();
        }
    }

    @AfterClass
    public static void afterAllTestsClear() {
        try {
            manager.connect(runParameters.getDatabaseName(), runParameters.getUserName(), runParameters.getPassword());
            manager.dropDatabase(TEST_DATABASE);
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
            manager.connect(TEST_DATABASE, runParameters.getUserName(), runParameters.getPassword());
            putDataToTestTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putDataToTestTable() throws SQLException {
        manager.clearTable(TEST_TABLE);
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
    public void testExit() {
        //given
        consoleInputStream.addLine("4"); // 4 - Exit (Main menu)
        consoleInputStream.addLine("y"); // answer "y"

        String connectionStatus = String.format(res_connectionStatus.getString("connection.status.database"),
                runParameters.getDatabaseName());
        String expectedMessage = res_common.getString("common.welcome") + "\n" +
                res_common.getString("common.try.connect.default.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.successful") + "\n" +
                connectionStatus + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_exit.getString("exit.question") + "\n" +
                res_common.getString("common.the.end") + "\n";

        //when
        SQLCmdMain.main(new String[0]);
        //then
        assertEquals(expectedMessage, getData());
    }

//    @Test
//    public void testTerminatedExit() {
//        //given
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testListWithoutConnection() {
//        //given
//        consoleInputStream.addLine("2");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\nNo any database connected.\n" +
//                MAIN_MENU +
//                // input - 2
//                "No one connection to database. Select \"DBConnect to database\" first.\n" +
//                "\nNo any database connected.\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testSelectTableWithoutConnect() {
//        //given
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 3
//                "No one connection to database. Select \"DBConnect to database\" first.\n" +
//                "\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testUnsupportedChoiceInMainMenu() {
//        //given
//        consoleInputStream.addLine("0");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 0
//                "\n" +
//                "Please choice correct number:\n" +
//                "1 - Connect to database\n" +
//                "2 - List all table names\n" +
//                "3 - Select table to work\n" +
//                "4 - Exit\n" +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testConnect() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\nNo any database connected.\n" +
//                MAIN_MENU +
//                // input - 1
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">.\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testListWithConnect() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("2");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                // input - TEST_DATABASE
//                "Enter you login:\n" +
//                // input - javauser
//                "Enter you password:\n" +
//                // input - test
//                "Connection successful!\n" +
//                "\n" +
//                //input - 2
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                "Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testListWithConnectToEmptyDatabase() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE_EMPTY);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("2");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                // input - TEST_DATABASE_EMPTY
//                "Enter you login:\n" +
//                // input - javauser
//                "Enter you password:\n" +
//                // input - test
//                "Connection successful!\n" +
//                "\n" +
//                //input - 2
//                "Connected to database: <" + TEST_DATABASE_EMPTY + ">\n" +
//                MAIN_MENU +
//                "There are no tables consoleInputStream the database <" + TEST_DATABASE_EMPTY + ">\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE_EMPTY + ">\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testSelectTableWithConnect() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine(TEST_TABLE);
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\nNo any database connected.\n" +
//                MAIN_MENU +
//                // input - 1
//                "Connection successful!\n" +
//                "\n" +
//                //input - 3
//                "Connected to database: <" + TEST_DATABASE + ">.\n" +
//                MAIN_MENU +
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                //input - users
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testSelectTableToEmptyDatabase() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE_EMPTY);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE_EMPTY
//                "Enter you login:\n" +
//                //input - USER_NAME
//                "Enter you password:\n" +
//                //input - USER_PASSWORD
//                "Connection successful!\n" +
//                "\n" +
//                //input - 3
//                "Connected to database: <" + TEST_DATABASE_EMPTY + ">\n" +
//                MAIN_MENU +
//                "There are no tables consoleInputStream the database <" + TEST_DATABASE_EMPTY + ">\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE_EMPTY + ">\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testPrintTableWithConnect() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine(TEST_TABLE);
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - USER_NAME
//                "Enter you password:\n" +
//                //input - USER_PASSWORD
//                "Connection successful!\n" +
//                "\n" +
//                //input - 3
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                //input - TEST_TABLE
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - 1
//                "|id|name        |password    |\n" +
//                "+--+------------+------------+\n" +
//                "|1 |Semen Petrov|qwert       |\n" +
//                "|2 |Bob Marley  |pass1       |\n" +
//                "|3 |Coca Cola   |pepsithebest|\n" +
//                "\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testSelectIncorrectTableName() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("usersa");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test +
//                "Connection successful!\n" +
//                "\n" +
//                //input - 3
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                //input - usersa
//                "Enter correct table name. Available tables:\n" +
//                "[users, staff]\n\n" +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testClearTable() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("users");
//        consoleInputStream.addLine("4");
//        consoleInputStream.addLine("y");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                //input - 3
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                "Do you really want to clear table <users>? <y/n>\n" +
//                //input - y
//                "Table clear successful.\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testClearTableWithCancell() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("users");
//        consoleInputStream.addLine("4");
//        consoleInputStream.addLine("n");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                //input - 3
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                "Do you really want to clear table <users>? <y/n>\n" +
//                //input - y
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testReturn() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("3");
//        consoleInputStream.addLine("users");
//        consoleInputStream.addLine("5");
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                //input - 3
//                "Enter table name. Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">. Selected table: <users>\n" +
//                TABLE_MENU +
//                //input - 5
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }
//
//    @Ignore
//    @Test
//    public void testConnectToAnotherDatabase() {
//        //given
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("1");
//        consoleInputStream.addLine(TEST_DATABASE_EMPTY);
//        consoleInputStream.addLine(USER_NAME);
//        consoleInputStream.addLine(USER_PASSWORD);
//        consoleInputStream.addLine("exit");
//        //when
//        SQLCmdMain.main(new String[0]);
//        //then
//        assertEquals("Welcome to SQLCmd!\n" +
//                "\n" +
//                MAIN_MENU +
//                // input - 1
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - TEST_DATABASE
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
//                MAIN_MENU +
//                "Enter database name, login and password.\n" +
//                "Type 'exit' for exit program.\n" +
//                "\n" +
//                "Please, enter database name:\n" +
//                //input - postgers
//                "Enter you login:\n" +
//                //input - javauser
//                "Enter you password:\n" +
//                //input - test
//                "Connection successful!\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE_EMPTY + ">\n" +
//                MAIN_MENU +
//                //input - exit
//                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
//    }

    private String getData() {
        try {
            String result = new String(consoleOutputStream.toByteArray(), "UTF-8");
            consoleOutputStream.reset();
            return result.replaceAll(System.lineSeparator(), "\n");
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }

    private static void resourceSetUp() {
        res_common = getResourceBundle("common");
        res_exit = getResourceBundle("Exit");
        res_connectionStatus = getResourceBundle("connectionStatus");
        res_DBConnect = getResourceBundle("DBConnect");
    }

    private static ResourceBundle getResourceBundle(String resource) {
        return ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + resource, new UTF8Control());
    }
}
