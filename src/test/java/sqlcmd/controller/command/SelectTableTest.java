package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SelectTableTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        ConnectionStatusHelper connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new SelectTable(connectionStatusHelper, manager, view);
    }

    @Test
    public void testSelectTableWithoutConnectionToDatabase() throws Exception {
        //given
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();

        //then
        shouldPrint("[No one connection to database. Select \"Connect to database\" first.\n]");
    }

    @Test
    public void testSelectTableWithEmptyDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();

        //then
        shouldPrint("[There are no tables in the database <null>\n]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

}