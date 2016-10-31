package SQLcmd.view;

import SQLcmd.exception.InterruptOperationException;

public interface View {
    void writeMessage(String message);

    String readLine() throws InterruptOperationException;

}
