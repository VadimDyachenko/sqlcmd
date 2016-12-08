package ua.com.vadim.SQLcmd.controller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ua.com.vadim.SQLcmd.controller.command.Command;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
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
    private static final String TABLE_NAME = "users";
    private static String userName;
    private static String password;
    private static final String resPath = "ua.com.vadim.SQLcmd.controller.resources.interface.";
    private static ResourceBundle res_common;
    private static ResourceBundle res_dbConnect;
    private static ResourceBundle res_connectionStatus;
    private static ResourceBundle res_dbSelectTable;
    private static ResourceBundle res_exit;

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
        res_common = ResourceBundle.getBundle(resPath + "common", new UTF8Control());
        res_dbConnect = ResourceBundle.getBundle(resPath + "DBConnect", new UTF8Control());
        res_connectionStatus = ResourceBundle.getBundle(resPath + "connectionStatus", new UTF8Control());
        res_exit = ResourceBundle.getBundle(resPath + "Exit", new UTF8Control());
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
                res_common.getString("common.welcome") + ", " +
                res_common.getString("common.try.connect.default.parameters") + ", " +
                res_dbConnect.getString("dbconnect.successful") + ", " +
                String.format(res_connectionStatus.getString("connection.status.database"), databaseName) + " " +
                String.format(res_connectionStatus.getString("connection.status.table"), "null") +
                "\n, " +
                res_common.getString("common.choice.operation") + ", " +
                res_common.getString("common.table.menu") + ", " +
                res_exit.getString("exit.question") + ", " +
                res_common.getString("common.the.end") +
                "]";
        when(runParameters.isTableLevel()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(view.readLine()).thenReturn("4", "y");
        when(runParameters.getLanguageResourcePath()).thenReturn("ua.com.vadim.SQLcmd.controller.resources.interface.");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        doCallRealMethod().when(localeSelector).setEnglishLocale();

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
                res_common.getString("common.welcome") + ", " +
                res_common.getString("common.try.connect.default.parameters") + ", " +
                res_dbConnect.getString("dbconnect.successful") + ", " +
                String.format(res_connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                res_common.getString("common.choice.operation") + ", " +
                res_common.getString("common.main.menu") + ", " +
                res_exit.getString("exit.question") + ", " +
                res_common.getString("common.the.end") +
                "]";
        //when
        when(view.readLine()).thenReturn("4", "y");
        when(runParameters.getLanguageResourcePath()).thenReturn("ua.com.vadim.SQLcmd.controller.resources.interface.");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        doThrow(new UnsupportedLanguageException()).when(localeSelector).setLocale(anyString());
        doCallRealMethod().when(localeSelector).setEnglishLocale();

        controller.run();

        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testRunWithExitException() {
        //given
        String expectedMessage = "[" +
                res_common.getString("common.welcome") + ", " +
                res_common.getString("common.try.connect.default.parameters") + ", " +
                res_dbConnect.getString("dbconnect.successful") + ", " +
                String.format(res_connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                res_common.getString("common.choice.operation") + ", " +
                res_common.getString("common.main.menu") + ", " +
                res_common.getString("common.the.end") +
                "]";
        //when
        when(runParameters.getLanguageResourcePath()).thenReturn("ua.com.vadim.SQLcmd.controller.resources.interface.");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        doThrow(new ExitException()).when(view).readLine();
        doCallRealMethod().when(localeSelector).setEnglishLocale();
        controller.run();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testRunWithIllegalArgumentException() {
        //given
        String expectedMessage = "[" +
                res_common.getString("common.welcome") + ", " +
                res_common.getString("common.try.connect.default.parameters") + ", " +
                res_dbConnect.getString("dbconnect.successful") + ", " +
                String.format(res_connectionStatus.getString("connection.status.database"), databaseName) +
                " \n, " +
                res_common.getString("common.choice.operation") + ", " +
                res_common.getString("common.main.menu") + ", " +
                res_common.getString("common.choice.correct") + ", " +
                res_common.getString("common.main.menu") + ", " +
                res_exit.getString("exit.question") + ", " +
                res_common.getString("common.the.end") +
                "]";
        //when
        when(runParameters.getLanguageResourcePath()).thenReturn("ua.com.vadim.SQLcmd.controller.resources.interface.");
        when(runParameters.getServerIP()).thenReturn(serverIP);
        when(runParameters.getServerPort()).thenReturn(serverPort);
        when(runParameters.getUserName()).thenReturn(userName);
        when(runParameters.getPassword()).thenReturn(password);
        when(runParameters.getDatabaseName()).thenReturn(databaseName);
        when(view.readLine()).thenThrow(new IllegalArgumentException()).thenReturn("4", "y");
        doCallRealMethod().when(localeSelector).setEnglishLocale();
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
