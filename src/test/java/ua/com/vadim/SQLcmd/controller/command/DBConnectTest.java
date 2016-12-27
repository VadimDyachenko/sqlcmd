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
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class DBConnectTest extends AbstractCommandTest{
    private static RunParameters parameters;
    private static ResourceBundle resources;
    private View view;
    private DatabaseManager manager;
    private Command command;
    private String normalRunMessages;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setup() {
        manager = mock(PostgresDBManager.class);
        view = mock(Console.class);
        command = new DBConnect(parameters, manager, view);
    }

    @Test
    public void testConnectDefaultParameter() {
        //given
        when(manager.isConnected()).thenReturn(false);

        //when
        command.execute();

        //then
        shouldPrint("[Connection successful!]");
    }

    @Test
    public void testConnectDefaultParameterWithSQLException() throws SQLException {
        //given
        when(manager.isConnected()).thenReturn(false);
        doThrow(new SQLException("Some SQLException")).when(manager).connect(anyString(), anyString(), anyString());

        //when
        try {
            command.execute();
            fail("expected excetion");
        } catch (ExitException e) {
            assertEquals(null, e.getMessage());
        }

        //then
        shouldPrint("[Connection failed: Some SQLException," +
                " Please check connection parameter in sqlcmd.properties file and try again.]");
    }

    @Test
    public void testConnectNewParameter() throws SQLException {
        //given
        when(manager.isConnected()).thenReturn(true);

        //when
        command.execute();

        //then
        shouldPrint("[Please, enter new connection parameters or type <exit> to exit programm.," +
                " Enter database name:, Enter you login:, Enter you password:]");
    }

    @Test
    public void testConnectNewParameterWithSQLException() throws SQLException {
        //given
        doThrow(new SQLException("Some SQLException")).when(manager).connect(anyString(), anyString(), anyString());

        //when
        when(manager.isConnected()).thenReturn(true);
        command.execute();
        //then
        shouldPrint("[Please, enter new connection parameters or type <exit> to exit programm., " +
                "Enter database name:, Enter you login:, Enter you password:, " +
                "Connection failed: Some SQLException, Try again or type <exit> to exit programm.]");
    }

    @Override
    View getView() {
        return view;
    }
}
