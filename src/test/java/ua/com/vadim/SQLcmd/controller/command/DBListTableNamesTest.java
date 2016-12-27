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

public class DBListTableNamesTest extends AbstractCommandTest{
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
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new DBListTableNames(parameters, manager, view);
    }

    @Test
    public void testListTableNamesWithoutConnectionToDatabase() throws Exception {
        //given
        when(manager.isConnected()).thenReturn(false);

        //when
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
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);

        //when
        command.execute();

        //then
        shouldPrint("[Available tables:, [id, names, password]]");
    }

    @Test
    public void testListTableNamesWithEmptyDatabase() throws Exception {
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
    public void testListTableNamesWithSQLException() throws Exception {
        //given
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException("Some SQLException")).when(manager).getAllTableNames();

        //when
        command.execute();

        //then
        shouldPrint("[Failure, because:Some SQLException, There are no any tables in the database.]");
    }

    @Override
    View getView() {
        return view;
    }
}
