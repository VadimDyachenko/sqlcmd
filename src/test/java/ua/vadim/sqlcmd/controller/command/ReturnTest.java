package ua.vadim.sqlcmd.controller.command;

import org.junit.Before;
import org.junit.Test;
import ua.vadim.sqlcmd.controller.ConnectionStatusHelper;
import ua.vadim.sqlcmd.exception.InterruptOperationException;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class ReturnTest {
    private Command command;
    private ConnectionStatusHelper connectionStatusHelper;

    @Before
    public void setUp() {
        connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new Return(connectionStatusHelper);
    }

    @Test
    public void testReturn() throws InterruptOperationException {
        //given
        connectionStatusHelper.setTableLevel(true);
        //when
        command.execute();
        //then
        assertFalse(connectionStatusHelper.isTableLevel());
    }
}