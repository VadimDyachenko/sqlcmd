package ua.com.vadim.SQLcmd.controller.command;


import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DBConnectTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setup() {
        manager = mock(PostgresDBManager.class);
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
        doThrow(new SQLException()).when(manager).connect(anyString(), anyString(), anyString());
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
