package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.UTF8Control;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TableUpdateRecordTest {
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private View view;
    private Command command;
    private Set<String> columnNames;
    private final String tableName = "tableA";
    private final String helpInfo = resources.getString("table.update.record.help");
    private final String successful = resources.getString("table.update.successful");
    private final String column = "[id, names, password]";
    private final String availableColumn = resources.getString("table.update.available.column");
    private final String availableColumnFormatted = String.format(availableColumn, column);
    private final String createUpdateResult = "{names:Some Name, password:somepassword}";

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("TableUpdateRecord", new UTF8Control());
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TableUpdateRecord(runParameters, manager, view);
        runParameters.setTableName(tableName);
        DataSet[] tableData = new DataSetImpl[1];
        DataSet dataSet = new DataSetImpl();
        dataSet.put("id", 1);
        dataSet.put("names", "TestName");
        dataSet.put("password", "qwerty");
        tableData[0] = dataSet;
        columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
    }

    @Test
    public void testTableUpdateRecord() throws SQLException {
        //given
        String successfulFormatted = String.format(successful, createUpdateResult);
        String expectedMessage = String.format("[%s, %s, %s]", helpInfo, availableColumnFormatted, successfulFormatted);
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|Some Name|password|somepassword");
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableUpdateRecordWrongId() throws SQLException {
        //given
        String successfulFormatted = String.format(successful, createUpdateResult);
        String wrongIdMessage = resources.getString("table.update.record.number.format");
        String expectedMessage = String.format("[%s, %s, %s, %s]",
                                            helpInfo,
                                            availableColumnFormatted,
                                            wrongIdMessage,
                                            successfulFormatted);
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|wrong|names|Some Name|password|somepassword",
                                         "id|1|names|Some Name|password|somepassword" );
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableUpdateRecordWrongArguments() throws SQLException {
        //given
        String successfulFormatted = String.format(successful, createUpdateResult);
        String wrongArguments = resources.getString("table.update.incorrect.data");
        String exceptionMessage = resources.getString("table.update.record.invalid.number");
        String expectedMessage = String.format("[%s, %s, %s%s, %s]",
                helpInfo,
                availableColumnFormatted,
                wrongArguments,
                exceptionMessage,
                successfulFormatted);
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|password|somepassword",
                "id|1|names|Some Name|password|somepassword" );
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTableIsEmpty() throws Exception {
        //given
        String emptyTablesFormatted = String.format(resources.getString("table.update.empty.table"), tableName);
        String expectedMessage = String.format("[%s]", emptyTablesFormatted);
        columnNames = new LinkedHashSet<>();
        //when
        when(manager.getTableColumnNames(tableName)).thenReturn(columnNames);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testUpdateRecordWithSQLException() throws SQLException {
        //given
        String exceptionMessage = "Some SQLException";
        String failMessage = resources.getString("table.update.record.failed");
        String expectedMessage = String.format("[%s%s]",
                failMessage,
                exceptionMessage);
        //when
        when(manager.getTableColumnNames(tableName)).thenThrow(new SQLException(exceptionMessage));
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
