package ua.com.vadim.SQLcmd.view;

public interface View {
    void writeMessage(String message);

    void writeData(String message);

    String readLine();
}
