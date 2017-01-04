package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.Locale;

import static org.junit.Assert.fail;
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

    @Test
    public void testExitYes() {
        //when
        try {
            when(view.read()).thenReturn("y");
            command.execute();
            fail();
        } catch (ExitException e) {
            //NOP
        }
    }

    @Test
    public void testExitYesWithSQLException() throws SQLException, ExitException {
        //given
        when(view.read()).thenReturn("y");
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
        when(view.read()).thenReturn("n");

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
