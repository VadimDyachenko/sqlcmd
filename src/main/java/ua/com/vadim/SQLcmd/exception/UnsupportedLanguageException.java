package ua.com.vadim.SQLcmd.exception;

public class UnsupportedLanguageException extends Exception {
    public UnsupportedLanguageException() {
    }

    @Override
    public String getMessage() {
        return "Unsupported language parameter in sqlcmd.properties file. Please, change it to \"en\" or \"ru\"";
    }
}
