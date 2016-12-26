package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DBSelectTableTest {
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private View view;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("DBSelectTable", new UTF8Control());
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new DBSelectTable(runParameters, manager, view);
    }

    @Test
    public void testSelectTableWithoutConnectionToDatabase() throws Exception {
        //given
        String expectedMessage = String.format("[%s]", resources.getString("dbselect.no.connection"));
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testSelectTableWithEmptyDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        String expectedMessage = String.format("[%s]", resources.getString("dbselect.no.tables"));
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();

        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testGetAvailableTableNamesWithSQLException() throws Exception{
        //given
        String exceptionMessage = "Some SQLException";
        String expectedMessage = String.format("[%s%s, %s]",
                resources.getString("dbselect.failure"),
                exceptionMessage,
                resources.getString("dbselect.no.tables"));
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenThrow(new SQLException(exceptionMessage));
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testSelectTableExecuteWithNormalParameters() throws Exception{
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("tableA");
        availableTables.add("tableB");
        String result = "[tableA, tableB]";
        String expectedMessage = String.format("[%s, %s]",
                resources.getString("dbselect.enter.name.tables"),
                result);
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("tableA");
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testSelectTableExecuteWithWrongParameters() throws Exception {
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("tableA");
        availableTables.add("tableB");
        String result = "[tableA, tableB]";
        String expectedMessage = String.format("[%s, %s, %s, %s]",
                resources.getString("dbselect.enter.name.tables"),
                result,
                resources.getString("dbselect.enter.correct.name.tables"),
                result);
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("bla-bla", "tableB");
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