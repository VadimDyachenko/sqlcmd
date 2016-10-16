package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ListTableNamesTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        ConnectionStatusHelper connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new ListTableNames(connectionStatusHelper, manager, view);
    }

    @Test
    public void testListTableNamesWithoutConnectionToDatabase() throws Exception {
        //given
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();

        //then
        shouldPrint("[No one connection to database. Select \"Connect to database\" first.\n]");
    }

    @Test
    public void testListTableNamesWithConnectionToDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        tableNames.add("id");
        tableNames.add("names");
        tableNames.add("password");
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();

        //then
        shouldPrint("[Available tables:, [id, names, password]\n]");
    }

    @Test
    public void testListTableNamesWithEmptyDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();

        //then
        shouldPrint("[There are no tables in the database <null>\n]");
    }

    @Test
    public void testListTableNamesWithSQLException() throws Exception {
        //given
        //when
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException()).when(manager).getAllTableNames();
        command.execute();

        //then
        shouldPrint("[Failure, because null, There are no tables in the database <null>\n]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

}
