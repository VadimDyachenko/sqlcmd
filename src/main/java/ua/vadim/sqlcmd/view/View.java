package ua.vadim.sqlcmd.view;

import ua.vadim.sqlcmd.exception.InterruptOperationException;

public interface View {
    void writeMessage(String message);

    String readLine() throws InterruptOperationException;

}
