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
import java.util.Locale;

import static org.mockito.Mockito.*;

public class TableClearTest extends AbstractCommandTest{
    private static RunParameters parameters;
    private View view;
    private DatabaseManager manager;
    private Command command;
    private final String testTableName = "tableA";

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableClear(parameters, manager, view);
        parameters.setTableName(testTableName);
    }

    @Test
    public void testTableClearWithYesAnswer() throws Exception {
        //given
        when(manager.isConnected()).thenReturn(true);
        when(view.readLine()).thenReturn("y");

        //when
        command.execute();

        //then
        shouldPrint("[Do you really want to clear table <tableA>? <y/n>, Table clear successful.]");
    }

    @Test
    public void testTableClearWithSQLException() throws Exception {
        //given
        doThrow(new SQLException("Some SQLException")).when(manager).clearTable(testTableName);
        when(view.readLine()).thenReturn("y");

        //then
        command.execute();

        //then
        shouldPrint("[Do you really want to clear table <tableA>? <y/n>," +
                " Table is not cleared because:Some SQLException]");
        //when
    }

    @Override
    View getView() {
        return view;
    }
}
