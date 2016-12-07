package ua.com.vadim.SQLcmd.integration;

import org.junit.*;

import ua.com.vadim.SQLcmd.SQLCmdMain;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static DatabaseManager manager;
    private ConfigurableInputStream consoleInputStream;
    private ByteArrayOutputStream consoleOutputStream;

    private static final String SERVER_IP = "192.168.1.5";
    private static final String SERVER_PORT = "5432";
    private static final String WORK_DATABASE = "sqlcmd";
    private static final String USER_NAME = "javauser";
    private static final String USER_PASSWORD = "test";
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_DATABASE_EMPTY = "test_db_empty";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE + " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";

    private static ResourceBundle res_common;
    private static ResourceBundle res_exit;
    private static ResourceBundle res_connectionStatus;
    private static ResourceBundle res_DBConnect;
    private static ResourceBundle res_DBListTableNames;
    private static ResourceBundle res_DBSelectTable;


    @BeforeClass
    public static void beforeAllTestSetUp() {
        manager = new PostgresDBManager(SERVER_IP, SERVER_PORT);
        Locale.setDefault(Locale.ENGLISH);
        ;
        resourceSetUp();
        try {
            manager.connect(WORK_DATABASE, USER_NAME, USER_PASSWORD);
            manager.dropDatabase(TEST_DATABASE);
            manager.dropDatabase(TEST_DATABASE_EMPTY);
            manager.createDatabase(TEST_DATABASE);
            manager.createDatabase(TEST_DATABASE_EMPTY);
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
            manager.connect(WORK_DATABASE, USER_NAME, USER_PASSWORD);
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
            manager.connect(TEST_DATABASE, USER_NAME, USER_PASSWORD);
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
                WORK_DATABASE);
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

    @Test
    public void testTerminatedExit() {
        //given
        consoleInputStream.addLine("exit");

        String connectionStatus = String.format(res_connectionStatus.getString("connection.status.database"),
                WORK_DATABASE);
        String expectedMessage = res_common.getString("common.welcome") + "\n" +
                res_common.getString("common.try.connect.default.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.successful") + "\n" +
                connectionStatus + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_common.getString("common.the.end") + "\n";
        //when
        SQLCmdMain.main(new String[0]);
        //then
        assertEquals(expectedMessage, getData());
    }

    @Test
    public void testConnectDatabase() {
        //given
        consoleInputStream.addLine("1"); // 1 - Connect to Database (Main menu)
        consoleInputStream.addLine(TEST_DATABASE);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("4"); // 4 - Exit (Main menu)
        consoleInputStream.addLine("y"); // answer "y"

        String connectionStatusFirst = String.format(res_connectionStatus.getString("connection.status.database"),
                WORK_DATABASE);
        String connectionStatusSecond = String.format(res_connectionStatus.getString("connection.status.database"),
                TEST_DATABASE);
        String expectedMessage = res_common.getString("common.welcome") + "\n" +
                res_common.getString("common.try.connect.default.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.successful") + "\n" +
                connectionStatusFirst + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_DBConnect.getString("dbconnect.enter.connection.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.enter.database.name") + "\n" +
                res_DBConnect.getString("dbconnect.enter.login") + "\n" +
                res_DBConnect.getString("dbconnect.enter.password") + "\n" +
                connectionStatusSecond + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_exit.getString("exit.question") + "\n" +
                res_common.getString("common.the.end") + "\n";

        //when
        SQLCmdMain.main(new String[0]);
        //then
        assertEquals(expectedMessage, getData());
    }

    @Test
    public void testListAllTableNames() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("2"); // 2 - List Table Name
        consoleInputStream.addLine("4"); // 4 - Exit
        consoleInputStream.addLine("y");

        String connectionStatus = String.format(res_connectionStatus.getString("connection.status.database"),
                TEST_DATABASE);
        String expectedMessage = getStartupMessages() +
                res_DBListTableNames.getString("dblist.available.tables") + "\n" +
                "[" + TEST_TABLE + "]\n" +
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

    @Test
    public void testSelectTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit
        consoleInputStream.addLine("y");

        String connectionDBStatus = String.format(res_connectionStatus.getString("connection.status.database"),
                TEST_DATABASE);
        String connectionTableStatus = String.format(res_connectionStatus.getString("connection.status.table"),
                TEST_TABLE);
        String connectionStatus = connectionDBStatus + " " + connectionTableStatus;
        String expectedMessage = getStartupMessages() +
                res_DBSelectTable.getString("dbselect.enter.name.tables") + "\n" +
                "[" + TEST_TABLE + "]\n" +
                connectionStatus + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                connectionDBStatus + " \n\n" +
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
//                "Available tables:\n" +
//                "[users, staff]\n" +
//                "\n" +
//                "Connected to database: <" + TEST_DATABASE + ">\n" +
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

    private String getStartupMessages() {
        String connectionStatusFirst = String.format(res_connectionStatus.getString("connection.status.database"),
                WORK_DATABASE);
        String connectionStatusSecond = String.format(res_connectionStatus.getString("connection.status.database"),
                TEST_DATABASE);
        String result = res_common.getString("common.welcome") + "\n" +
                res_common.getString("common.try.connect.default.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.successful") + "\n" +
                connectionStatusFirst + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_DBConnect.getString("dbconnect.enter.connection.parameters") + "\n" +
                res_DBConnect.getString("dbconnect.enter.database.name") + "\n" +
                res_DBConnect.getString("dbconnect.enter.login") + "\n" +
                res_DBConnect.getString("dbconnect.enter.password") + "\n" +
                connectionStatusSecond + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n";
        return result;
    }

    private void setStartupAnswers() {
        consoleInputStream.addLine("1"); // 1 - Connect to Database (Main menu)
        consoleInputStream.addLine(TEST_DATABASE);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
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

    private static void resourceSetUp() {
        res_common = getResourceBundle("common");
        res_exit = getResourceBundle("Exit");
        res_connectionStatus = getResourceBundle("connectionStatus");
        res_DBConnect = getResourceBundle("DBConnect");
        res_DBListTableNames = getResourceBundle("DBListTableNames");
        res_DBSelectTable = getResourceBundle("DBSelectTable");
    }

    private static ResourceBundle getResourceBundle(String resource) {
        return ResourceBundle.getBundle("ua.com.vadim.SQLcmd.controller.resources.interface." + resource, new UTF8Control());
    }
}
