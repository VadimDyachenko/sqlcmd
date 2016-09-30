package sqlcmd.view;

public enum Operation {
    LOGIN,
    LIST_TABLE,
    TABLE_PRINT,
    TABLE_CHANGE,
    EXIT;

    public static Operation getAllowableOperation(Integer i) {
        switch(i)
        {
            case 1:
                return Operation.LIST_TABLE;
            case 2:
                return Operation.TABLE_PRINT;
            case 3:
                return Operation.TABLE_CHANGE;
            case 4:
                return Operation.EXIT;
            default:
                throw new IllegalArgumentException();
        }
    }
}
