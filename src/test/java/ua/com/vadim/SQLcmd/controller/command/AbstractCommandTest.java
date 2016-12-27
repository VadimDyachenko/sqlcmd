package ua.com.vadim.SQLcmd.controller.command;

import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.View;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public abstract class AbstractCommandTest {

    protected void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(getView(), atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }

    abstract View getView();
}
