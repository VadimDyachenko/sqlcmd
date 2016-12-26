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

import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;

public class DBConnectTest extends AbstractCommandTest{
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private Command command;
    private String normalRunMessages;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("DBConnect", new UTF8Control());
    }

    @Before
    public void setup() {
        manager = mock(PostgresDBManager.class);
        view = mock(Console.class);
        command = new DBConnect(runParameters, manager, view);
        String m1 = resources.getString("dbconnect.enter.connection.parameters");
        String m2 = resources.getString("dbconnect.enter.database.name");
        String m3 = resources.getString("dbconnect.enter.login");
        String m4 = resources.getString("dbconnect.enter.password");
        normalRunMessages = String.format("%s, %s, %s, %s", m1, m2, m3, m4);
    }

    @Test
    public void testConnectDefaultParameter() {
        //given
        String expectedMessage = String.format("[%s]", resources.getString("dbconnect.successful"));
        //when
        when(manager.isConnected()).thenReturn(false);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test (expected = ExitException.class)
    public void testConnectDefaultParameterWithSQLException() throws SQLException {
        //given
        String exceptionMessage = "Some SQLException";
        String failedMessage = resources.getString("dbconnect.failed");
        String tryAgainMessage = resources.getString("dbconnect.default.failed");
        String expectedMessage = String.format("[%s %s, %s]",failedMessage, exceptionMessage,
                tryAgainMessage);
        //when
        when(manager.isConnected()).thenReturn(false);
        doThrow(new SQLException(exceptionMessage)).when(manager).connect(anyString(), anyString(), anyString());
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testConnectNewParameter() throws SQLException {
        //given
        String expectedMessage = String.format("[%s]", normalRunMessages);
        //when
        when(manager.isConnected()).thenReturn(true);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testConnectNewParameterWithSQLException() throws SQLException {
        //given
        String exceptionMessage = "Some SQLException";
        String failedMessage = resources.getString("dbconnect.failed");
        String tryAgainMessage = resources.getString("dbconnect.try.again");
        String expectedMessage = String.format("[%s, %s %s, %s]", normalRunMessages, failedMessage,
                exceptionMessage, tryAgainMessage);
        //when
        doThrow(new SQLException(exceptionMessage)).when(manager).connect(anyString(), anyString(), anyString());
        when(manager.isConnected()).thenReturn(true);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }
}
