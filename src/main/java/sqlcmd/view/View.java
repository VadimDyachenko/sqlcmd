package sqlcmd.view;

import sqlcmd.exception.InterruptOperationException;

/**
 * Created by vadim on 30.09.16.
 */
public interface View {
    void writeMessage(String message);

    String readLine() throws InterruptOperationException;

    void printExitMessage();

    Operation askOperation() throws InterruptOperationException;
}
