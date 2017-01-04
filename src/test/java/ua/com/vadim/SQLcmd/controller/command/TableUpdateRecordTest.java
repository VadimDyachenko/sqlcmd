package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TableUpdateRecordTest extends AbstractCommandTest {
    private static RunParameters parameters;
    private View view;
    private DatabaseManager manager;
    private Command command;
    private Set<String> columnNames;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableUpdateRecord(parameters, manager, view);

        parameters.setTableName("Table");

        columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
    }

    @Test
    public void testTableUpdateRecord() throws SQLException, ExitException {
        //given
        when(manager.getTableColumnNames("Table")).thenReturn(columnNames);
        when(view.read()).thenReturn("id|1|names|Some Name|password|somepassword");

        //when
        command.execute();

        //then
        shouldPrint("[Enter data to update table record.\n" +
                "Input format: id|Value_id|ColumnName1|Value1| ... |ColumnNameN|ValueN," +
                " Available column name: [id, names, password], " +
                "Record {names:Some Name, password:somepassword} was update successful.]");
    }

    @Test
    public void testTableUpdateRecordWrongId() throws SQLException, ExitException {
        //given
        when(manager.getTableColumnNames("Table")).thenReturn(columnNames);
        when(view.read()).thenReturn("id|wrong|names|Some Name|password|somepassword",
                                         "id|1|names|Some Name|password|somepassword" );

        //when
        command.execute();

        //then
        shouldPrint("[Enter data to update table record.\n" +
                "Input format: id|Value_id|ColumnName1|Value1| ... |ColumnNameN|ValueN," +
                " Available column name: [id, names, password]," +
                " Wrong id. Parameter <id> must be numeric (int). Try again.," +
                " Record {names:Some Name, password:somepassword} was update successful.]");
    }

    @Test
    public void testTableUpdateRecordWrongArguments() throws SQLException, ExitException {
        //given
        when(manager.getTableColumnNames("Table")).thenReturn(columnNames);
        when(view.read()).thenReturn("id|1|names|password|somepassword",
                "id|1|names|Some Name|password|somepassword" );

        //when
        command.execute();

        //then
        shouldPrint("[Enter data to update table record.\n" +
                "Input format: id|Value_id|ColumnName1|Value1| ... |ColumnNameN|ValueN," +
                " Available column name: [id, names, password]," +
                " Incorrect data. Invalid number of parameters. Try again., " +
                "Record {names:Some Name, password:somepassword} was update successful.]");
    }

    @Test
    public void testTableIsEmpty() throws Exception {
        //given
        columnNames = new LinkedHashSet<>();
        when(manager.getTableColumnNames("Table")).thenReturn(columnNames);

        //when
        command.execute();

        //then
        shouldPrint("[Table <Table> does not contain any records. Command UpdateRecord cancelled.]");
    }

    @Test
    public void testUpdateRecordWithSQLException() throws SQLException, ExitException {
        //given
        when(manager.getTableColumnNames("Table")).thenThrow(new SQLException("Some SQLException"));

        //when
        command.execute();

        //then
        shouldPrint("[Record is not updated because:Some SQLException]");
    }

    @Override
    View getView() {
        return view;
    }
}
