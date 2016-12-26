package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class ExitTest {
    private static RunParameters runParameters;
    private static ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        res = ResourceBundle.getBundle(/*runParameters.getLanguageResourcePath() + */"Exit", new UTF8Control());
    }

    @Before
    public void setUp() {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new Exit(manager, view);
    }

    @Test(expected = ExitException.class)
    public void testExitYes() {
        //given
//        String expectedMessage = String.format("[%s, %s]",
//                res_exit.getString("exit.question"),
//                res_common.getString("common.the.end"));

        //when
        when(view.readLine()).thenReturn("y");
        command.execute();

        //then
    }


    @Test
    public void testExitYesWithSQLException() throws SQLException {
        //given
        String expectedMessage = String.format("[%s]", res.getString("exit.question"));
        //when
        when(view.readLine()).thenReturn("y");
        when(manager.isConnected()).thenReturn(true);
        doThrow(new SQLException()).when(manager).disconnect();
        try {
            command.execute();
        } catch (ExitException e) {
            //NOP
        }
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testExitNo() throws ExitException {
        //given
        String expectedMessage = String.format("[%s]", res.getString("exit.question"));
        //when
        when(view.readLine()).thenReturn("n");
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
