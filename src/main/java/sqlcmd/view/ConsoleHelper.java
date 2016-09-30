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

        while (true) {
            writeMessage("1 - List all table names");
            writeMessage("2 - Print table");
            writeMessage("3 - Change table");
            writeMessage("4 - Exit");  //TODO вырезать exit

            String choice = readLine();
            try {
                Integer numOfChoice = Integer.parseInt(choice);
                return Operation.getAllowableOperation(numOfChoice);
            } catch (IllegalArgumentException e) {
                writeMessage("\nPlease choise correct number:");
            }
        }
    }
}
