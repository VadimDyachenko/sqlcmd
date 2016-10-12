package sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import sqlcmd.controller.Controller;

import static org.mockito.Mockito.*;

public class ReturnTest {
    private Command command;
    private Controller controller;

    @Before
    public void setUp() {
        controller = mock(Controller.class);
        command = new Return(controller);
    }

    @Test
    public void testReturn() {
        //given
        //when

        //then
    }
}