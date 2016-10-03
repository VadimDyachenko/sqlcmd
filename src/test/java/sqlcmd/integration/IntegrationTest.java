package sqlcmd.integration;



import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sqlcmd.Controller;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;


public class IntegrationTest {

    private  ConfigurableInputStream in;
    private  ByteArrayOutputStream out;

    @Before
    public void setup(){
        in = new ConfigurableInputStream();
        out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));
    }

    @Test
    public void testExit(){
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
    public void testTerminatedExit(){
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
