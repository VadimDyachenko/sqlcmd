package SQLcmd.controller.command;


import SQLcmd.controller.RunParameters;
import SQLcmd.model.DatabaseManager;
import SQLcmd.model.JDBCPostgreDatabaseManager;
import SQLcmd.view.Console;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import SQLcmd.view.View;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DBConnectTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setup() {
        manager = mock(JDBCPostgreDatabaseManager.class);
        view = mock(Console.class);
        RunParameters runParameters = mock(RunParameters.class);
        command = new DBConnect(runParameters, manager, view);
    }

    @Test
    public void testConnect() throws Exception {
        //given
        //when
        command.execute();
        //then
        shouldPrint("[Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                ", Please, enter database name:, Enter you login:, Enter you password:, " +
                "Connection successful!\n]");
    }

    @Test
    public void testConnectWithSQLException() throws Exception {
        //given
        //when
        doThrow(new SQLException()).when(manager).connect(anyString(), anyString(), anyString(),
                                                            anyString(), anyString(), anyString());
        when(manager.isConnected()).thenReturn(true);
        command.execute();
        //then
        shouldPrint("[Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n" +
                ", Please, enter database name:, " +
                "Enter you login:, Enter you password:, " +
                "Connection failed: null, Try again or type <exit>.]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
