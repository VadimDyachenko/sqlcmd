package ua.com.vadim.SQLcmd.controller.command;

import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.View;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class AbstractCommandTest {
    protected View view;

    protected void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
