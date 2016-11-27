package ua.com.vadim.SQLcmd.exception;

public class BreakException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Operation canceled";
    }
}
