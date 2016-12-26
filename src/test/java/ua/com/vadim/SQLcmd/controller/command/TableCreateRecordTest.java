package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static org.mockito.Mockito.*;

public class TableCreateRecordTest extends AbstractCommandTest {
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private Command command;
    private Set<String> columnNames;
    private final String tableName = "tableA";
    private final String helpInfo = resources.getString("table.create.record.help");
    private final String successful = resources.getString("table.create.successful");
    private final String column = "[id, names, password]";
    private final String availableColumn = resources.getString("table.create.available.column");
    private final String availableColumnFormatted = String.format(availableColumn, column);
    private final String createRecordResult = "{id:1, names:Some Name, password:somepassword}";

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("TableCreateRecord", new UTF8Control());
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableCreateRecord(runParameters, manager, view);
        runParameters.setTableName(tableName);
        columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
    }

    @Test
    public void testTableCreateRecordWithNormalParameters() throws SQLException {
        //given
        String successfulFormatted = String.format(successful, createRecordResult);
        String expectedMessage = String.format("[%s, %s, %s]", helpInfo, availableColumnFormatted, successfulFormatted);
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|Some Name|password|somepassword");
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableCreateRecordWithWrongParameters() throws Exception {
        //given
        String incorrectMessage = resources.getString("table.create.incorrect.data");
        String wrongNumberParameter = resources.getString("table.create.record.invalid.number");
        String successfulFormatted = String.format(successful, createRecordResult);
        String expectedMessage = String.format("[%s, %s, %s%s, %s]",
                helpInfo,
                availableColumnFormatted,
                incorrectMessage,
                wrongNumberParameter,
                successfulFormatted);
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("names|Some Name|password|somepassword",
                "id|1|names|Some Name|password|somepassword");
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testGetAvailableColumnNamesWithSQLException() throws SQLException {
        //given
        String exceptionMessage = "Some SQLException";
        String failMessage = resources.getString("table.create.record.failed");
        String expectedMessage = String.format("[%s %s]",
                failMessage,
                exceptionMessage);
        //when
        when(manager.getTableColumnNames(tableName)).thenThrow(new SQLException(exceptionMessage));
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableIsEmpty() throws Exception {
        //given
        String emptyTablesFormatted = String.format(resources.getString("table.create.empty.table"), tableName);
        String expectedMessage = String.format("[%s]", emptyTablesFormatted);
        columnNames = new LinkedHashSet<>();
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }
}
