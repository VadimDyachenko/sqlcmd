package ua.com.vadim.SQLcmd.controller.command;

import org.junit.BeforeClass;
import org.junit.Ignore;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class TableCreateRecordTest {
    private static RunParameters runParameters;
    private static ResourceBundle res;
    private DatabaseManager manager;
    private View view;
    private Command command;
    private Set<String> columnNames;
    private String tableName = "tableA";
    private String helpInfo = res.getString("table.create.record.help");
    private String successful = res.getString("table.create.successful");
    private String column = "[id, names, password]";
    private String availableColumn = res.getString("table.create.available.column");
    private String availableColumnFormatted = String.format(availableColumn, column);
    private String createRecordResult = "{id:1, names:Some Name, password:somepassword}";

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        res = ResourceBundle.getBundle(runParameters.getLanguageResourcePath() + "TableCreateRecord", new UTF8Control());
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
        String incorrectMessage = res.getString("table.create.incorrect.data");
        String wrongNumberParameter = res.getString("table.create.record.invalid.number");
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
        String failMessage = res.getString("table.create.record.failed");
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
        String emptyTablesFormatted = String.format(res.getString("table.create.empty.table"), tableName);
        String expectedMessage = String.format("[%s]", emptyTablesFormatted);
        columnNames = new LinkedHashSet<>();
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
