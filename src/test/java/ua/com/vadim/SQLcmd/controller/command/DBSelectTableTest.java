package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Mockito.*;

public class DBSelectTableTest extends AbstractCommandTest{
    private static RunParameters parameters;
    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new DBSelectTable(parameters, manager, view);
    }

    @Test
    public void testSelectTableWithoutConnectionToDatabase() throws Exception {
        //given
        when(manager.isConnected()).thenReturn(false);

        //when
        command.execute();

        //then
        shouldPrint("[No one connection to database. Select \"Connect to database\" first.\n]");
    }

    @Test
    public void testSelectTableWithEmptyDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);

        //when
        command.execute();

        //then
        shouldPrint("[There are no any tables in the database.]");
    }

    @Test
    public void testGetAvailableTableNamesWithSQLException() throws Exception{
        //given
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenThrow(new SQLException("Some SQLException"));

        //when
        command.execute();

        //then
        shouldPrint("[Failure, because:Some SQLException, There are no any tables in the database.]");
    }

    @Test
    public void testSelectTableExecuteWithNormalParameters() throws Exception{
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("tableA");
        availableTables.add("tableB");
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("tableA");

        //when
        command.execute();

        //then
        shouldPrint("[Enter table name. Available tables:, [tableA, tableB]]");
    }

    @Test
    public void testSelectTableExecuteWithWrongParameters() throws Exception {
        //given
        Set<String> availableTables = new LinkedHashSet<>();
        availableTables.add("tableA");
        availableTables.add("tableB");
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(availableTables);
        when(view.readLine()).thenReturn("bla-bla", "tableB");

        //when
        command.execute();

        //then
        shouldPrint("[Enter table name. Available tables:, [tableA, tableB]," +
                " Enter correct table name. Available tables:, [tableA, tableB]]");
    }

    @Override
    View getView() {
        return view;
    }
}