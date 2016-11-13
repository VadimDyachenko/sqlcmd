package SQLcmd.integration;

import SQLcmd.controller.PropertiesLoader;
import org.junit.Before;
import org.junit.Test;
import SQLcmd.SQLcmdMain;
import SQLcmd.controller.RunParameters;
import SQLcmd.model.DataSet;
import SQLcmd.model.DataSetImpl;
import SQLcmd.model.DatabaseManager;
import SQLcmd.model.PostgreDatabaseManager;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private ConfigurableInputStream consoleInputStream;
    private ByteArrayOutputStream consoleOutputStream;
    private static final String DATABASE_NAME = "sqlcmd";
    private static final String EMPTY_DATABASE_NAME = "postgres";
    private static final String USER_NAME = "javauser";
    private static final String USER_PASSWORD = "test";
    private static final String TABLE_NAME = "users";

    private static final String MAIN_MENU =
                    "Please choose an operation desired or type 'EXIT' for exiting\n" +
                    "1 - Connect to database\n" +
                    "2 - List all table names\n" +
                    "3 - Select table to work\n" +
                    "4 - Exit\n";

    private static final String TABLE_MENU =
                    "Please choose an operation desired or type 'EXIT' for exiting\n" +
                    "1 - Print table data\n" +
                    "2 - Create table record\n" +
                    "3 - Update table record\n" +
                    "4 - Clear table\n" +
                    "5 - Return to previous menu\n";

    @Before
    public void setUp() {
        consoleInputStream = new ConfigurableInputStream();
        consoleOutputStream = new ByteArrayOutputStream();
        System.setIn(consoleInputStream);
        System.setOut(new PrintStream(consoleOutputStream));
        RunParameters runParameters = new PropertiesLoader().getParameters();
        DatabaseManager manager = new PostgreDatabaseManager(runParameters.getDriver(),
                                                                runParameters.getServerIP(),
                                                                runParameters.getServerPort());
        try {
            manager.connect(
                    runParameters.getDatabaseName(),
                    runParameters.getUserName(),
                    runParameters.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
        runParameters.setTableName(TABLE_NAME);

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
    public void testExit() {
        //given
        consoleInputStream.addLine("4");
        consoleInputStream.addLine("y");
        //when
        SQLcmdMain.main(new String[0]);

        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\nNo any database connected.\n" +
                MAIN_MENU +
                // input - 4
                "Do you really want to exit? <y/n>\n" +
                // input - y
                "Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testTerminatedExit() {
        //given
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithoutConnection() {
        //given
        consoleInputStream.addLine("2");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\nNo any database connected.\n" +
                MAIN_MENU +
                // input - 2
                "No one connection to database. Select \"DBConnect to database\" first.\n" +
                "\nNo any database connected.\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableWithoutConnect() {
        //given
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 3
                "No one connection to database. Select \"DBConnect to database\" first.\n" +
                "\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testUnsupportedChoiceInMainMenu() {
        //given
        consoleInputStream.addLine("0");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 0
                "\n" +
                "Please choice correct number:\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n"+
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testConnect() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\nNo any database connected.\n" +
                MAIN_MENU +
                // input - 1
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">.\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithConnect() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("2");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                // input - DATABASE_NAME
                "Enter you login:\n" +
                // input - javauser
                "Enter you password:\n" +
                // input - test
                "Connection successful!\n" +
                "\n" +
                //input - 2
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithConnectToEmptyDatabase() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(EMPTY_DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("2");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                // input - EMPTY_DATABASE_NAME
                "Enter you login:\n" +
                // input - javauser
                "Enter you password:\n" +
                // input - test
                "Connection successful!\n" +
                "\n" +
                //input - 2
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "There are no tables consoleInputStream the database <" + EMPTY_DATABASE_NAME + ">\n" +
                "\n" +
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableWithConnect() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine("3");
        consoleInputStream.addLine(TABLE_NAME);
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\nNo any database connected.\n" +
                MAIN_MENU +
                // input - 1
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <" + DATABASE_NAME + ">.\n" +
                MAIN_MENU +
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                //input - users
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableToEmptyDatabase() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(EMPTY_DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - EMPTY_DATABASE_NAME
                "Enter you login:\n" +
                //input - USER_NAME
                "Enter you password:\n" +
                //input - USER_PASSWORD
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "There are no tables consoleInputStream the database <" + EMPTY_DATABASE_NAME + ">\n" +
                "\n" +
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testPrintTableWithConnect() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine(TABLE_NAME);
        consoleInputStream.addLine("1");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - USER_NAME
                "Enter you password:\n" +
                //input - USER_PASSWORD
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                //input - TABLE_NAME
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - 1
                "|id|name        |password    |\n" +
                "+--+------------+------------+\n" +
                "|1 |Semen Petrov|qwert       |\n" +
                "|2 |Bob Marley  |pass1       |\n" +
                "|3 |Coca Cola   |pepsithebest|\n" +
                "\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectIncorrectTableName() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("usersa");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test +
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                //input - usersa
                "Enter correct table name. Available tables:\n" +
                "[users, staff]\n\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testClearTable() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("users");
        consoleInputStream.addLine("4");
        consoleInputStream.addLine("y");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - 3
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                "Do you really want to clear table <users>? <y/n>\n" +
                //input - y
                "Table clear successful.\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testClearTableWithCancell() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("users");
        consoleInputStream.addLine("4");
        consoleInputStream.addLine("n");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - 3
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                "Do you really want to clear table <users>? <y/n>\n" +
                //input - y
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testReturn() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("3");
        consoleInputStream.addLine("users");
        consoleInputStream.addLine("5");
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - 3
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - 5
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testConnectToAnotherDatabase() {
        //given
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("1");
        consoleInputStream.addLine(EMPTY_DATABASE_NAME);
        consoleInputStream.addLine(USER_NAME);
        consoleInputStream.addLine(USER_PASSWORD);
        consoleInputStream.addLine("exit");
        //when
        SQLcmdMain.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - DATABASE_NAME
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - postgers
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <"+ EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
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
