package sqlcmd.view;

import sqlcmd.exception.InterruptOperationException;

public interface View {
    void writeMessage(String message);

    String readLine() throws InterruptOperationException;

    void printExitMessage();

}
