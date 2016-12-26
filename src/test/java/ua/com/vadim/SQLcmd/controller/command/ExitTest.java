package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;


public class ExitTest extends AbstractCommandTest {
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private View view;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        resources = ResourceBundle.getBundle("Exit", new UTF8Control());
    }

    @Before
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new Exit(manager, view);
    }

    @Test(expected = ExitException.class)
    public void testExitYes() {
        //when
        when(view.readLine()).thenReturn("y");
        command.execute();
    }

    @Test
    public void testExitYesWithSQLException() throws SQLException {
        //given
        String expectedMessage = String.format("[%s]", resources.getString("exit.question"));
        //when
        when(view.readLine()).thenReturn("y");
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException()).when(manager).disconnect();
        try {
            command.execute();
        } catch (ExitException e) {
            //NOP
        }
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testExitNo() throws ExitException {
        //given
        String expectedMessage = String.format("[%s]", resources.getString("exit.question"));
        //when
        when(view.readLine()).thenReturn("n");
        command.execute();

        //then
        shouldPrint(expectedMessage);
    }
}
