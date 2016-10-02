package sqlcmd.command;

public enum Operation {
    CONNECT,
    LIST_TABLE,
    SELECT_TABLE,
    TABLE_PRINT,
    TABLE_CHANGE,
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
}
