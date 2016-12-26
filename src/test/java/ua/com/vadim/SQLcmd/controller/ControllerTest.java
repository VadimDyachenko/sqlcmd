package ua.com.vadim.SQLcmd.controller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {
    private static String serverIP;
    private static String serverPort;
    private static String databaseName;
    private static String userName;
    private static String password;
    private static ResourceBundle common;
    private static ResourceBundle dbConnect;
    private static ResourceBundle connectionStatus;
    private static ResourceBundle exit;

    @Mock
    private RunParameters runParameters;
    @Mock
    private LocaleSelector localeSelector;
    @Mock
    private View view;

    @InjectMocks
    private Controller controller = new Controller(view);

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        setTestRunParameters();
        common = ResourceBundle.getBundle("common", new UTF8Control());
        dbConnect = ResourceBundle.getBundle("DBConnect", new UTF8Control());
        connectionStatus = ResourceBundle.getBundle("connectionStatus", new UTF8Control());
        exit = ResourceBundle.getBundle("Exit", new UTF8Control());
    }

    private static void setTestRunParameters() {
        RunParameters runTestParameters = new PropertiesLoader().getParameters();
        serverIP = runTestParameters.getServerIP();
        serverPort = runTestParameters.getServerPort();
        databaseName = runTestParameters.getDatabaseName();
        userName = runTestParameters.getUserName();
        password = runTestParameters.getPassword();
    }

    /**
     * Выполнение этого теста не совпадает с реальной логикой последовательности команд
     * Это сделано умышленно для упрощения проверки работы Controller на уровне меню таблиц (isTableLevel() == true)
     */
    @Test
    public void testRun() {
        //given
        String expectedMessage = "[" +
                common.getString("common.welcome") + ", " +
                common.getString("common.try.connect.default.parameters") + ", " +
                dbConnect.getString("dbconnect.successful") + ", " +
                String.format(connectionStatus.getString("connection.status.database"), databaseName) + " " +
                String.format(connectionStatus.getString("connection.status.table"), "null") +
                "\n, " +
                common.getString("common.choice.operation") + ", " +
                common.getString("common.table.menu") + ", " +
                exit.getString("exit.question") + ", " +
                common.getString("common.the.end") +
                "]";
        when(runParameters.isTableLevel()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(view.readLine()).thenReturn("4", "y");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        controller.run();

        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testRunWithUnsupportedLanguageException() {
        //given
        String expectedMessage = "[" +
                "Unsupported language parameter in sqlcmd.properties file." + ", " +
                "Exit the program and change it to available variant: []" + ", " +
                "Current interface language setting to [en]\n" + ", " +
                common.getString("common.welcome") + ", " +
                common.getString("common.try.connect.default.parameters") + ", " +
                dbConnect.getString("dbconnect.successful") + ", " +
                String.format(connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                common.getString("common.choice.operation") + ", " +
                common.getString("common.main.menu") + ", " +
                exit.getString("exit.question") + ", " +
                common.getString("common.the.end") +
                "]";
        //when
        when(view.readLine()).thenReturn("4", "y");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        doThrow(new UnsupportedLanguageException()).when(localeSelector).setLocale(anyString());
        controller.run();

        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testRunWithExitException() {
        //given
        String expectedMessage = "[" +
                common.getString("common.welcome") + ", " +
                common.getString("common.try.connect.default.parameters") + ", " +
                dbConnect.getString("dbconnect.successful") + ", " +
                String.format(connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                common.getString("common.choice.operation") + ", " +
                common.getString("common.main.menu") + ", " +
                common.getString("common.the.end") +
                "]";
        //when
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        doThrow(new ExitException()).when(view).readLine();
        controller.run();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testRunWithIllegalArgumentException() {
        //given
        String expectedMessage = "[" +
                common.getString("common.welcome") + ", " +
                common.getString("common.try.connect.default.parameters") + ", " +
                dbConnect.getString("dbconnect.successful") + ", " +
                String.format(connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                common.getString("common.choice.operation") + ", " +
                common.getString("common.main.menu") + ", " +
                common.getString("common.choice.correct") + ", " +
                common.getString("common.main.menu") + ", " +
                exit.getString("exit.question") + ", " +
                common.getString("common.the.end") +
                "]";
        //when
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        when(view.readLine()).thenThrow(new IllegalArgumentException()).thenReturn("4", "y");

        controller.run();
        //then
        shouldPrint(expectedMessage);
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
