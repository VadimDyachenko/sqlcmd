package ua.com.vadim.SQLcmd.view;

import ua.com.vadim.SQLcmd.exception.ExitException;

public interface View {
    void writeMessage(String message);

    String read();
}
