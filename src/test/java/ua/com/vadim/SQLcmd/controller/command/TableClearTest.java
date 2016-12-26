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
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;

public class TableClearTest extends AbstractCommandTest{
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private Command command;
    private final String testTableName = "tableA";
    private String questionFormatted;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("TableClear", new UTF8Control());
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableClear(runParameters, manager, view);

        String question = resources.getString("table.clear.question");
        runParameters.setTableName(testTableName);
        questionFormatted = String.format(question, testTableName);
    }

    @Test
    public void testTableClearWithYesAnswer() throws Exception {
        //given
        String successful = resources.getString("table.clear.successful");
        String expectedMessage = String.format("[%s, %s]", questionFormatted, successful);
        //when
        when(manager.isConnected()).thenReturn(true);
        when(view.readLine()).thenReturn("y");
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableClearWithSQLException() throws Exception {
        //given
        String exceptionMessage = "Some SQLException";
        String fail = resources.getString("table.clear.failed");
        String expectedMessage = String.format("[%s, %s%s]", questionFormatted, fail, exceptionMessage);
        //then
        doThrow(new SQLException(exceptionMessage)).when(manager).clearTable(testTableName);
        when(view.readLine()).thenReturn("y");
        command.execute();
        //then
        shouldPrint(expectedMessage);
        //when
    }
}
