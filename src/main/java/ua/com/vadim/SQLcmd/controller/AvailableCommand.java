package ua.com.vadim.SQLcmd.controller;

enum AvailableCommand {
    DB_CONNECT,
    DB_AVAILABLE_TABLE,
    DB_SELECT_TABLE,
    TABLE_PRINT,
    TABLE_CREATE_RECORD,
    TABLE_UPDATE_RECORD,
    TABLE_CLEAR,
    PRINT_CONNECTION_STATUS,
    RETURN,
    EXIT;

    public static AvailableCommand getMainCommand(Integer i) {
        switch(i)
        {
            case 1:
                return AvailableCommand.DB_CONNECT;
            case 2:
                return AvailableCommand.DB_AVAILABLE_TABLE;
            case 3:
                return AvailableCommand.DB_SELECT_TABLE;
            case 4:
                return AvailableCommand.EXIT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static AvailableCommand getTableCommand(Integer i) {
        switch(i)
        {
            case 1:
                return AvailableCommand.TABLE_PRINT;
            case 2:
                return AvailableCommand.TABLE_CREATE_RECORD;
            case 3:
                return AvailableCommand.TABLE_UPDATE_RECORD;
            case 4:
                return AvailableCommand.TABLE_CLEAR;
            case 5:
                return AvailableCommand.RETURN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
