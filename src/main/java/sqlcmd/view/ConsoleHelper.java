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
    public String readLine() throws InterruptOperationException{
        String result = "";
        try {
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

    @Override
    public Operation askOperation() throws InterruptOperationException {
        writeMessage("Please choose an operation desired or type 'EXIT' for exiting");
        writeMessage("1 - List all table names");
        writeMessage("2 - List table");

        while (true) {
            String choice = readLine();
            try
            {
                Integer numOfChoice = Integer.parseInt(choice);
                if (numOfChoice == 0) throw new IllegalArgumentException();
                return Operation.getAllowableOperation(numOfChoice);
            } catch (Exception e) {
                writeMessage("invalid.data");
            }

        }
    }
}
