package ua.com.vadim.SQLcmd.integration;

import org.junit.*;
import ua.com.vadim.SQLcmd.SQLCmdMain;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
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

    private static final String SERVER_IP = "localhost";
    private static final String SERVER_PORT = "5432";
    private static final String WORK_DATABASE = "sqlcmd";
    private static final String USER_NAME = "javauser";
    private static final String USER_PASSWORD = "test";
    private static final String TEST_DATABASE = "test_db_sqlcmd";
    private static final String TEST_DATABASE_EMPTY = "test_db_empty";
    private static final String TEST_TABLE = "test_table_users";
    private static final String CREATE_TABLE_QUERY = TEST_TABLE +
            " (id SERIAL PRIMARY KEY," +
            " name VARCHAR (25) UNIQUE NOT NULL," +
            " password VARCHAR (25) NOT NULL)";

    private static ResourceBundle res_common;
    private static ResourceBundle res_exit;
    private static ResourceBundle res_connectionStatus;
    private static ResourceBundle res_DBConnect;
    private static ResourceBundle res_DBListTableNames;
    private static ResourceBundle res_DBSelectTable;
    private static ResourceBundle res_TableCreateRecord;
    private static ResourceBundle res_TableUpdateRecord;
    private static ResourceBundle res_TableClear;


    @BeforeClass
    public static void beforeAllTestSetUp() {
        manager = new PostgresDBManager(SERVER_IP, SERVER_PORT);
        setLocale();
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

    private static void setLocale() {
        RunParameters runParameters = new PropertiesLoader().getParameters();
        if (runParameters.getInterfaceLanguage().toLowerCase().equals("ru")) {
            Locale.setDefault(new Locale("ru", "RU"));
        } else {
            Locale.setDefault(Locale.ENGLISH);
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
        String connectionStatusSecond = getConnectionDBStatus();
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
        consoleInputStream.addLine("2"); // 2 - List Table Names (Main Menu)
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String expectedMessage = getStartupMessages() +
                res_DBListTableNames.getString("dblist.available.tables") + "\n" +
                "[" + TEST_TABLE + "]\n" +
                getConnectionDBStatus() + " \n\n" +
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
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String expectedMessage = getStartupMessages() +
                res_DBSelectTable.getString("dbselect.enter.name.tables") + "\n" +
                "[" + TEST_TABLE + "]\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
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
    public void testPrintTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("1"); // 1 - Print Table Data
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String printResult = "|id|name        |password    |\n" +
                "+--+------------+------------+\n" +
                "|1 |Semen Petrov|qwert       |\n" +
                "|2 |Bob Marley  |pass1       |\n" +
                "|3 |Coca Cola   |pepsithebest|";

        String expectedMessage = getStartupTableMessages() +
                printResult + "\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
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
    public void testCreateRecord() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("2"); // 2 - Create table record
        consoleInputStream.addLine("id|8|name|ABC|password|DEF");
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String availableColumn = String.format(res_TableCreateRecord.getString("table.create.available.column"),
                "[id, name, password]");
        String resultCreateRecord = String.format(res_TableCreateRecord.getString("table.create.successful"),
                "{id:8, name:ABC, password:DEF}");
        String expectedMessage = getStartupTableMessages() +
                res_TableCreateRecord.getString("table.create.record.help") + "\n" +
                availableColumn + "\n" +
                resultCreateRecord + "\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
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
    public void testUpdateRecord() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("3"); // 3 - Update table record
        consoleInputStream.addLine("id|1|name|Serj|password|pass");
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String availableColumn = String.format(res_TableUpdateRecord.getString("table.update.available.column"),
                "[id, name, password]");
        String resultUpdateRecord = String.format(res_TableUpdateRecord.getString("table.update.successful"),
                "{name:Serj, password:pass}");
        String expectedMessage = getStartupTableMessages() +
                res_TableUpdateRecord.getString("table.update.record.help") + "\n" +
                availableColumn + "\n" +
                resultUpdateRecord + "\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
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
    public void testClearTable() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("4"); // 4 - Clear Table
        consoleInputStream.addLine("y");
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String questionClearTable = String.format(res_TableClear.getString("table.clear.question"),
                TEST_TABLE);
        String expectedMessage = getStartupTableMessages() +
                questionClearTable + "\n" +
                res_TableClear.getString("table.clear.successful") + "\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
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
    public void testUnsupportedChoiceInMainMenu() {
        //given
        setStartupAnswers();
        consoleInputStream.addLine("3"); // 3 - Select Table (Main Menu)
        consoleInputStream.addLine(TEST_TABLE);
        consoleInputStream.addLine("0"); // 0 - Unsupported choice
        consoleInputStream.addLine("5"); // 5 - Return to Main menu
        consoleInputStream.addLine("0"); // 0 - Unsupported choice
        consoleInputStream.addLine("4"); // 4 - Exit (Main Menu)
        consoleInputStream.addLine("y");

        String expectedMessage = getStartupTableMessages() +
                res_common.getString("common.choice.correct") + "\n" +
                res_common.getString("common.table.menu") + "\n" +
                getConnectionDBStatus() + " \n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_common.getString("common.choice.correct") + "\n" +
                res_common.getString("common.main.menu") + "\n" +
                res_exit.getString("exit.question") + "\n" +
                res_common.getString("common.the.end") + "\n";

        //when
        SQLCmdMain.main(new String[0]);
        //then
        assertEquals(expectedMessage, getData());
    }

    private String getStartupMessages() {
        String connectionStatusFirst = String.format(res_connectionStatus.getString("connection.status.database"),
                WORK_DATABASE);
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
                getConnectionDBStatus() + " \n\n" +
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

    private String getStartupTableMessages() {
        String result = getStartupMessages() +
                res_DBSelectTable.getString("dbselect.enter.name.tables") + "\n" +
                "[" + TEST_TABLE + "]\n" +
                getConnectionStatus() + "\n\n" +
                res_common.getString("common.choice.operation") + "\n" +
                res_common.getString("common.table.menu") + "\n";
        return result;
    }

    private String getConnectionStatus() {
        return getConnectionDBStatus() + " " + getConnectionTableStatus();
    }

    private String getConnectionTableStatus() {
        return String.format(res_connectionStatus.getString("connection.status.table"),
                TEST_TABLE);
    }

    private String getConnectionDBStatus() {
        return String.format(res_connectionStatus.getString("connection.status.database"),
                TEST_DATABASE);
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
        res_TableCreateRecord = getResourceBundle("TableCreateRecord");
        res_TableUpdateRecord = getResourceBundle("TableUpdateRecord");
        res_TableClear = getResourceBundle("TableClear");
    }

    private static ResourceBundle getResourceBundle(String resource) {
        return ResourceBundle.getBundle("ua.com.vadim.SQLcmd.controller.resources.interface." + resource, new UTF8Control());
    }
}
