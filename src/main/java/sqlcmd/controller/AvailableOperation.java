package sqlcmd.controller;

public enum AvailableOperation {
    CONNECT,
    LIST_TABLE,
    SELECT_TABLE,
    TABLE_PRINT,
    TABLE_CREATE_RECORD,
    TABLE_UPDATE_RECORD,
    TABLE_CLEAR,
    RETURN,
    EXIT;

    public static AvailableOperation getMainOperation(Integer i) {
        switch(i)
        {
            case 1:
                return AvailableOperation.CONNECT;
            case 2:
                return AvailableOperation.LIST_TABLE;
            case 3:
                return AvailableOperation.SELECT_TABLE;
            case 4:
                return AvailableOperation.EXIT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static AvailableOperation getTableOperation(Integer i) {
        switch(i)
        {
            case 1:
                return AvailableOperation.TABLE_PRINT;
            case 2:
                return AvailableOperation.TABLE_CREATE_RECORD;
            case 3:
                return AvailableOperation.TABLE_UPDATE_RECORD;
            case 4:
                return AvailableOperation.TABLE_CLEAR;
            case 5:
                return AvailableOperation.RETURN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
