package sqlcmd.integration;


import org.junit.Before;
import org.junit.Test;
import sqlcmd.Controller;
import sqlcmd.model.DataSet;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;


public class IntegrationTest {

    private ConfigurableInputStream in;
    private ByteArrayOutputStream out;
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
    public void setup() {
        in = new ConfigurableInputStream();
        out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        DatabaseManager manager = new JDBCDatabaseManager();
        try {
            manager.connect(DATABASE_NAME, USER_NAME, USER_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void testExit() {
        //given
        in.addLine("4");
        in.addLine("y");
        //when
        Controller.main(new String[0]);

        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 4
                "Do you really want to exit? <y/n>\n" +
                // input - y
                "Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testTerminatedExit() {
        //given
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithoutConnect() {
        //given
        in.addLine("2");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 2
                "No one connection to database. Select \"Connect to database\" first.\n" +
                "\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableWithoutConnect() {
        //given
        in.addLine("3");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 3
                "No one connection to database. Select \"Connect to database\" first.\n" +
                "\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testUnsupportedChoise() {
        //given
        in.addLine("0");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 0
                "\n" +
                "Please choise correct number:\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testConnect() {
        //given
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                MAIN_MENU +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                "Enter you login:\n" +
                "Enter you password:\n" +
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <" + DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithConnect() {
        //given
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("2");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(EMPTY_DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("2");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
                "There are no tables in the database <" + EMPTY_DATABASE_NAME + ">\n" +
                "\n" +
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableWithConnect() {
        //given
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine(TABLE_NAME);
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
                //input - users
                "Connected to database: <" + DATABASE_NAME + ">. Selected table: <users>\n" +
                TABLE_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableToEmptyDatabase() {
        //given
        in.addLine("1");
        in.addLine(EMPTY_DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
                "There are no tables in the database <" + EMPTY_DATABASE_NAME + ">\n" +
                "\n" +
                "Connected to database: <" + EMPTY_DATABASE_NAME + ">\n" +
                MAIN_MENU +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testPrintTableWithConnect() {
        //given
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine(TABLE_NAME);
        in.addLine("1");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine("usersa");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine("users");
        in.addLine("4");
        in.addLine("y");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine("users");
        in.addLine("4");
        in.addLine("n");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("3");
        in.addLine("users");
        in.addLine("5");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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
        in.addLine("1");
        in.addLine(DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("1");
        in.addLine(EMPTY_DATABASE_NAME);
        in.addLine(USER_NAME);
        in.addLine(USER_PASSWORD);
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
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

    public String getData() {
        try {
            String result = new String(out.toByteArray(), "UTF-8");
            out.reset();
            return result.replaceAll(System.lineSeparator(), "\n");
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}
