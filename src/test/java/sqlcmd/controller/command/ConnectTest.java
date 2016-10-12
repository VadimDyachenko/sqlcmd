package sqlcmd.controller.command;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sqlcmd.exception.InterruptOperationException;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;
import sqlcmd.view.Console;
import sqlcmd.view.View;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConnectTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setup() {
        manager = mock(JDBCDatabaseManager.class);
        view = mock(Console.class);
        command = new Connect(manager, view);
    }

    @Test
    public void connectWithExceptionTest() throws Exception{
        //given
        //when
        when(view.readLine()).thenReturn("sqlcmd", "javauser", "test");
        doThrow(new SQLException()).when(manager).connect("sqlcmd", "javauser", "test");
//        doThrow(new SQLException()).when(manager).connect(anyString(), anyString(), anyString());

        command.execute();
        //than
        shouldPrint("[Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                ", Please, enter database name:, " +
                "Enter you login:, Enter you password:, " +
                "Connection failed: null, Try again., " +
                "Please, enter database name:, Enter you login:, Enter you password:, " +
                "Connection successful!\n]");
    }

    @Test
    public void connectTest() throws Exception{
        //given
        //when
        command.execute();
        //than
        shouldPrint("[Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                ", Please, enter database name:, Enter you login:, Enter you password:, " +
                "Connection successful!\n]");
    }


    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
