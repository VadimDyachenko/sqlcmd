package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

import static org.mockito.Mockito.*;

public class DBListTableNamesTest extends AbstractCommandTest{
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("DBListTableNames", new UTF8Control());
    }

    @Before
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new DBListTableNames(runParameters, manager, view);
    }

    @Test
    public void testListTableNamesWithoutConnectionToDatabase() throws Exception {
        //given
        String expectedMessage = String.format("[%s]", resources.getString("dblist.no.connection"));
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testListTableNamesWithConnectionToDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        tableNames.add("id");
        tableNames.add("names");
        tableNames.add("password");
        String expectedResult = "[id, names, password]";
        String expectedMessage = String.format("[%s, %s]",
                resources.getString("dblist.available.tables"),
                expectedResult);
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();

        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testListTableNamesWithEmptyDatabase() throws Exception {
        //given
        Set<String> tableNames = new LinkedHashSet<>();
        String expectedMessage = String.format("[%s]", resources.getString("dblist.no.tables"));
        //when
        when(manager.isConnected()).thenReturn(true);
        when(manager.getAllTableNames()).thenReturn(tableNames);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testListTableNamesWithSQLException() throws Exception {
        //given
        String exceptionMessage = "Some SQLMessage";
        String expectedMessage = String.format("[%s%s, %s]",
                resources.getString("dblist.failure"),
                exceptionMessage,
                resources.getString("dblist.no.tables"));
        //when
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException(exceptionMessage)).when(manager).getAllTableNames();
        command.execute();

        //then
        shouldPrint(expectedMessage);
    }
}
