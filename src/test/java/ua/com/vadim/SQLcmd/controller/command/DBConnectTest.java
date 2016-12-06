package ua.com.vadim.SQLcmd.controller.command;


import org.junit.BeforeClass;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.model.PostgresDBManager;
import ua.com.vadim.SQLcmd.view.Console;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DBConnectTest {
    private static RunParameters runParameters;
    private static ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private Command command;
    private String normalRunMessages;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "DBConnect", new UTF8Control());
    }

    @Before
    public void setup() {
        manager = mock(PostgresDBManager.class);
        view = mock(Console.class);
        command = new DBConnect(runParameters, manager, view);
        String m1 = res.getString("dbconnect.enter.connection.parameters");
        String m2 = res.getString("dbconnect.enter.database.name");
        String m3 = res.getString("dbconnect.enter.login");
        String m4 = res.getString("dbconnect.enter.password");
        normalRunMessages = String.format("%s, %s, %s, %s", m1, m2, m3, m4);
    }

    @Test
    public void testConnectDefaultParameter() {
        //given
        String expectedMessage = String.format("[%s]", res.getString("dbconnect.successful"));
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
        String failedMessage = res.getString("dbconnect.failed");
        String tryAgainMessage = res.getString("dbconnect.default.failed");
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
        String failedMessage = res.getString("dbconnect.failed");
        String tryAgainMessage = res.getString("dbconnect.try.again");
        String expectedMessage = String.format("[%s, %s %s, %s]", normalRunMessages, failedMessage,
                exceptionMessage, tryAgainMessage);
        //when
        doThrow(new SQLException(exceptionMessage)).when(manager).connect(anyString(), anyString(), anyString());
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
