package sqlcmd.integration;


import org.junit.Before;
import org.junit.Test;
import sqlcmd.Controller;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;


public class IntegrationTest {

    private ConfigurableInputStream in;
    private ByteArrayOutputStream out;

    @Before
    public void setup() {
        in = new ConfigurableInputStream();
        out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));
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
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 2
                "No one connection to database. Select \"Connect to database\" first.\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 3
                "No one connection to database. Select \"Connect to database\" first.\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
        in.addLine("sqlcmd");
        in.addLine("javauser");
        in.addLine("test");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                "Enter you login:\n" +
                "Enter you password:\n" +
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testListWithConnect() {
        //given
        in.addLine("1");
        in.addLine("sqlcmd");
        in.addLine("javauser");
        in.addLine("test");
        in.addLine("2");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                "Enter you login:\n" +
                "Enter you password:\n" +
                "Connection successful!\n" +
                "\n" +
                //input - 2
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectTableWithConnect() {
        //given
        in.addLine("1");
        in.addLine("sqlcmd");
        in.addLine("javauser");
        in.addLine("test");
        in.addLine("3");
        in.addLine("users");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - sqlcmd
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test +
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                //input - users
                "Connected to database: <sqlcmd>. Selected table: <users>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Print table data\n" +
                "2 - Change table records\n" +
                "3 - Return to previous menu\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    @Test
    public void testSelectIncorrectTableName() {
        //given
        in.addLine("1");
        in.addLine("sqlcmd");
        in.addLine("javauser");
        in.addLine("test");
        in.addLine("3");
        in.addLine("usersa");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - sqlcmd
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test +
                "Connection successful!\n" +
                "\n" +
                //input - 3
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
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
    public void testReturnWithConnect() {
        //given
        in.addLine("1");
        in.addLine("sqlcmd");
        in.addLine("javauser");
        in.addLine("test");
        in.addLine("3");
        in.addLine("users");
        in.addLine("3");
        in.addLine("exit");
        //when
        Controller.main(new String[0]);
        //then
        assertEquals("Welcome to SQLCmd!\n" +
                "\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                // input - 1
                "Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                "\n" +
                "Please, enter database name:\n" +
                //input - sqlcmd
                "Enter you login:\n" +
                //input - javauser
                "Enter you password:\n" +
                //input - test
                "Connection successful!\n" +
                "\n" +
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                //input - 3
                "Enter table name. Available tables:\n" +
                "[users, staff]\n" +
                "\n" +
                "Connected to database: <sqlcmd>. Selected table: <users>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Print table data\n" +
                "2 - Change table records\n" +
                "3 - Return to previous menu\n" +
                //input - 3
                "Connected to database: <sqlcmd>\n" +
                "Please choose an operation desired or type 'EXIT' for exiting\n" +
                "1 - Connect to database\n" +
                "2 - List all table names\n" +
                "3 - Select table to work\n" +
                "4 - Exit\n" +
                //input - exit
                "Terminated. Thank you for using SQLCmd. Good luck.\n", getData());
    }

    public String getData() {
        try {
            String result = new String(out.toByteArray(), "UTF-8");
            out.reset();
            return result;
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}
