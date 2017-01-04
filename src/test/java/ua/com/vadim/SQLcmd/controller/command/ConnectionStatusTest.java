package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class ConnectionStatusTest extends AbstractCommandTest {
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
    public void setup() {
        manager = mock(PostgresDBManager.class);
        view = mock(Console.class);
        command = new ConnectionStatus(parameters, manager, view);
    }

    @Test
    public void testDBManagerNotConnected() throws SQLException, ExitException {
        //given
        manager.disconnect();
        when(manager.isConnected()).thenReturn(false);

        //when
        command.execute();
        //then
        shouldPrint("[No any database connected.\n" +
                "]");
    }

    @Test
    public void testIfTableLevelTrue() throws SQLException, ExitException {
        //given
        parameters.setDatabaseName("testDBName");
        parameters.setTableLevel(true);
        parameters.setTableName("tableA");
        when(manager.isConnected()).thenReturn(true);

        //when
        command.execute();
        //then
        shouldPrint("[\n" +
                "Connected to database: <testDBName>. Selected table: <tableA>.\n" +
                "]");
    }

    @Override
    View getView() {
        return view;
    }
}
