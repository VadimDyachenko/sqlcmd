package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import sqlcmd.exception.ExitException;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;


public class ExitTest {

    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new Exit(manager, view);
    }

    @Test
    public void testExitYes() throws Exception{
        //given
        //when
        when(view.readLine()).thenReturn("y");

        try {
            command.execute();
            fail("Expected ExitException");
        } catch (ExitException e) {
            e.getMessage();
        }

        //then
        shouldPrint("[Do you really want to exit? <y/n>, " +
                "Thank you for using SQLCmd. Good luck.]");
    }

    @Test
    public void testExitYesWhithSQLException() throws Exception{
        //given
        //when
        when(view.readLine()).thenReturn("y");
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException()).when(manager).disconnect();
        try {
            command.execute();
            fail("Expected ExitException");
        } catch (Exception e) {
            e.getMessage();
        }

        //then
        shouldPrint("[Do you really want to exit? <y/n>, " +
                "null, " +
                "Thank you for using SQLCmd. Good luck.]");
    }

    @Test
    public void testExitNo() {
        //given
        //when
        try {
            when(view.readLine()).thenReturn("n");
        } catch (InterruptOperationException e) {
            e.printStackTrace();
        }
        try {
            command.execute();
        } catch (Exception e) {
            e.getMessage();
        }

        //then
        shouldPrint("[Do you really want to exit? <y/n>]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
