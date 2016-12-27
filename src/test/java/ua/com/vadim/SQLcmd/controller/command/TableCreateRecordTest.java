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

public class TableCreateRecordTest extends AbstractCommandTest {
    private static RunParameters parameters;
    private View view;
    private DatabaseManager manager;
    private Command command;
    private Set<String> columnNames;
    private final String tableName = "tableA";

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableCreateRecord(parameters, manager, view);
        parameters.setTableName(tableName);
        columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
    }

    @Test
    public void testTableCreateRecordWithNormalParameters() throws SQLException {
        //given
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|Some Name|password|somepassword");

        //when
        command.execute();

        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN, " +
                "Available column name: [id, names, password]," +
                " Record {id:1, names:Some Name, password:somepassword} was create successful.]");
    }

    @Test
    public void testTableCreateRecordWithWrongParameters() throws Exception {
        //given
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("names|Some Name|password|somepassword",
                "id|1|names|Some Name|password|somepassword");

        //when
        command.execute();

        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN," +
                " Available column name: [id, names, password], Incorrect data. Invalid number of parameters.," +
                " Record {id:1, names:Some Name, password:somepassword} was create successful.]");
    }

    @Test
    public void testGetAvailableColumnNamesWithSQLException() throws SQLException {
        //given
        when(manager.getTableColumnNames(tableName)).thenThrow(new SQLException("Some SQLException"));

        //when
        command.execute();

        //then
        shouldPrint("[Record is not created because: Some SQLException]");
    }

    @Test
    public void testTableIsEmpty() throws Exception {
        //given
        columnNames = new LinkedHashSet<>();
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);

        //when
        command.execute();

        //then
        shouldPrint("[Table <tableA> does not contain any column. Command CreateRecord cancelled.]");
    }

    @Override
    View getView() {
        return view;
    }
}
