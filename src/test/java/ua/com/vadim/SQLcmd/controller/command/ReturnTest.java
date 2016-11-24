package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class ReturnTest {
    private Command command;
    private RunParameters runParameters;

    @Before
    public void setUp() {
        runParameters = mock(RunParameters.class);
        command = new Return(runParameters);
    }

    @Test
    public void testReturn() throws ExitException{
        //given
        runParameters.setTableLevel(true);
        //when
        command.execute();
        //then
        assertFalse(runParameters.isTableLevel());
    }
}