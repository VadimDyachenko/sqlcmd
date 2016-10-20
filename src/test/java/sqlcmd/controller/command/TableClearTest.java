package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TableClearTest {
    private DatabaseManager manager;
    private View view;
    private ConnectionStatusHelper connectionStatusHelper;
    private Command command;

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new TableClear(connectionStatusHelper, manager, view);
    }

    @Test
    public void testTableClearWithYesAnswer() throws Exception {
        //given
        //when
        when(manager.isConnected()).thenReturn(true);
        when(connectionStatusHelper.getCurrentTableName()).thenReturn("users");
        when(view.readLine()).thenReturn("y");
        command.execute();
        //then
        shouldPrint("[Do you really want to clear table <users>? <y/n>, Table clear successful.\n]");
    }

    @Test
    public void testTableClearWithSQLException() throws Exception {
        //given
        //then
        when(connectionStatusHelper.getCurrentTableName()).thenReturn("users");
        doThrow(new SQLException()).when(manager).clearCurrentTable("users");
        when(view.readLine()).thenReturn("y");
        command.execute();
        //then
        shouldPrint("[Do you really want to clear table <users>? <y/n>, Table not clear, null]");
        //when
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
