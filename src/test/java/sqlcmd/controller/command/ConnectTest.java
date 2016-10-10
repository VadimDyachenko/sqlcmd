package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


public class ConnectTest {

    private DatabaseManager manager;
    private View view;
    private Command command;
    private String DATABASE_NAME = "sqlcmd";
    private String USER_NAME = "javauser";
    private String USER_PASSWORD = "test";


    @Before

    public void setup() {
        manager = Mockito.mock(DatabaseManager.class);
        view = Mockito.mock(View.class);
        command = new Connect(manager, view);
    }

    @Test
    public void testConnect() {
        //given
        //when
//        try {
//            Mockito.when(view.readLine()).thenReturn(DATABASE_NAME);
//            Mockito.when(view.readLine()).thenReturn(USER_NAME);
//            Mockito.when(view.readLine()).thenReturn(USER_PASSWORD);
//            Mockito.when(manager.connect(DATABASE_NAME, USER_NAME, USER_PASSWORD)));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        try {
            command.execute();
//            fail("Expected ExitException");
        } catch (Exception e) {
            e.getMessage();
        }

        //then
        shouldPrint("[Enter database name, login and password.\n" +
                "Type 'exit' for exit program.\n, " +
                "Please, enter database name:, " +
                "Enter you login:, " +
                "Enter you password:, " +
                "Connection successful!\n]");
    }
    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

}
