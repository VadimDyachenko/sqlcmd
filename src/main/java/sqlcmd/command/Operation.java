package sqlcmd.command;

public enum Operation {
    CONNECT,
    LIST_TABLE,
    SELECT_TABLE,
    TABLE_PRINT,
    TABLE_CREATE_RECORD,
    TABLE_CLEAR,
    RETURN,
    EXIT;

    public static Operation getMainOperation(Integer i) {
        switch(i)
        {
            case 1:
                return Operation.CONNECT;
            case 2:
                return Operation.LIST_TABLE;
            case 3:
                return Operation.SELECT_TABLE;
            case 4:
                return Operation.EXIT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Operation getTableOperation(Integer i) {
        switch(i)
        {
            case 1:
                return Operation.TABLE_PRINT;
            case 2:
                return Operation.TABLE_CREATE_RECORD;
            case 3:
                return Operation.TABLE_CLEAR;
            case 4:
                return Operation.RETURN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
