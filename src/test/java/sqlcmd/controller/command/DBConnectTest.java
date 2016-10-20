package sqlcmd.controller.command;


import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.model.JDBCDatabaseManager;
import sqlcmd.view.Console;
import sqlcmd.view.View;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DBConnectTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setup() {
        manager = mock(JDBCDatabaseManager.class);
        view = mock(Console.class);
        ConnectionStatusHelper connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new DBConnect(connectionStatusHelper, manager, view);
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
