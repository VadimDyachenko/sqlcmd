package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.Locale;

import static org.mockito.Mockito.*;


public class ExitTest extends AbstractCommandTest {

    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
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
        when(view.readLine()).thenReturn("y");
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException()).when(manager).disconnect();

        //when
        try {
            command.execute();
        } catch (ExitException e) {
            //NOP
        }

        //then
        shouldPrint("[Do you really want to exit? <y/n>]");
    }

    @Test
    public void testExitNo() throws ExitException {
        //given
        when(view.readLine()).thenReturn("n");

        //when
        command.execute();

        //then
        shouldPrint("[Do you really want to exit? <y/n>]");
    }

    @Override
    View getView() {
        return view;
    }
}
