package sqlcmd.view;

import sqlcmd.exception.InterruptOperationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper implements View {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void writeMessage(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() throws InterruptOperationException {
        String result = "";
        try /*(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)))*/{
            result = reader.readLine();
            if ("exit".equalsIgnoreCase(result)) {
                throw new InterruptOperationException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void printExitMessage() {
        writeMessage("Terminated. Thank you for using SQLCmd. Good luck.");
    }
}
