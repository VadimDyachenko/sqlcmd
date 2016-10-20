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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DBSelectTableTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        ConnectionStatusHelper connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new DBSelectTable(connectionStatusHelper, manager, view);
    }

    @Test
    public void testSelectTableWithoutConnectionToDatabase() throws Exception {
        //given
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();

        //then
        shouldPrint("[No one connection to database. Select \"DBConnect to database\" first.\n]");
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

    @Test
    public void testGetAvailableTableNamesWithSQLException() throws Exception{
        //given
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenThrow(new SQLException());
        command.execute();
        //then
        shouldPrint("[Failure, because null, There are no tables in the database <null>\n]");
    }

    @Test
    public void testSelectTableExecuteWithNormalParameters() throws Exception{
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("users");
        availableTables.add("staff");
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("users");
        command.execute();
        //then
        shouldPrint("[Enter table name. Available tables:, [users, staff]\n]");
    }

    @Test
    public void testSelectTableExecuteWithWrongParameters() throws Exception{
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("users");
        availableTables.add("staff");
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("bla-bla", "users");
        command.execute();
        //then
        shouldPrint("[Enter table name. Available tables:, [users, staff]\n," +
                " Enter correct table name. Available tables:, [users, staff]\n]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}