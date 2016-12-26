package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ConnectionStatusTest {
    private static RunParameters runParameters;
    private static ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        res = ResourceBundle.getBundle("connectionStatus", new UTF8Control());
    }

    @Before
    public void setup() {
        manager = mock(PostgresDBManager.class);
        view = mock(Console.class);
        command = new ConnectionStatus(runParameters, manager, view);
    }

    @Test
    public void testDBManagerNotConnected() throws SQLException{
        //given
        String expectedMessage = String.format("[%s]", res.getString("connection.status.without.connection"));
        manager.disconnect();
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testIfTableLevelTrue() throws SQLException{
        //given
        String dataBaseName = "testDBName";
        String tableName = "tableA";
        runParameters.setDatabaseName(dataBaseName);
        runParameters.setTableLevel(true);
        runParameters.setTableName(tableName);
        String messageDBFormatted = String.format(res.getString("connection.status.database"), dataBaseName);
        String messageTableFormatted = String.format(res.getString("connection.status.table"), tableName);
        String expectedMessage = String.format("[%s %s\n]", messageDBFormatted, messageTableFormatted);
        //when
        when(manager.isConnected()).thenReturn(true);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
