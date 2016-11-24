package ua.com.vadim.SQLcmd.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Console implements View {

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void writeMessage(String message) {
        System.out.println(convertEncoding(message));
    }

    @Override
    public String readLine() {
        String result = "";
        try {
            result = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace(); //TODO причесать обработку исключения
        }
        return result;
    }


    /**
     * Этот метод-костыль используется для перекодировки символов сообщений прочитанных из файлов .properties
     * классом ResourceBundle.getBundle() Читать напрямую русские символы мне не удалось.
     * @param message
     * @return converted message
     */

    private String convertEncoding(String message) {
        try {
            return new String(message.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
