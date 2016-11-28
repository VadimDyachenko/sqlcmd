package ua.com.vadim.SQLcmd.view;

import ua.com.vadim.SQLcmd.exception.ExitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console implements View {

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void writeMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() throws ExitException {
        String result = "";
        try {
            result = reader.readLine();
            if ("exit".equalsIgnoreCase(result)) {
                throw new ExitException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
