package ua.com.vadim.SQLcmd.controller.command;

import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

public class TableCreateRecordTest {
    private DatabaseManager manager;
    private View view;
    private RunParameters runParameters;
    private Command command;

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        runParameters = mock(RunParameters.class);
        command = new TableCreateRecord(runParameters, manager, view);
    }

    @Test
    public void testTableCreateRecordWithNormalParameters() throws Exception {
        //given
        Set<String> columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
        //when
        when(runParameters.getTableName()).thenReturn("users");
        when(manager.getTableColumnNames("users")).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|Some Name|password|somepassword");
        //doNothing().when(manager).createTableRecord("users", anyObject());
        command.execute();
        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n" +
                ", Available column name for table <users> :, [id, names, password]," +
                " Record {id:1, names:Some Name, password:somepassword} was create successful in table <users>\n]");
    }

    @Test
    public void testTableCreateRecordWithWrongParameters() throws Exception {
        //given
        Set<String> columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
        //when
        when(runParameters.getTableName()).thenReturn("users");
        when(manager.getTableColumnNames("users")).thenReturn(columnNames);
        when(view.readLine()).thenReturn("names|Some Name|password|somepassword",
                "id|1|names|Some Name|password|somepassword");
        command.execute();
        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n" +
                ", Available column name for table <users> :, [id, names, password], " +
                "Incorrect data. Invalid number of parameters, " +
                "Record {id:1, names:Some Name, password:somepassword} was create successful in table <users>\n]");
    }

    @Test
    public void testTableCreateRecordWithSQLException1() throws Exception {
        //given
        //when
        when(runParameters.getTableName()).thenReturn("users");
        when(manager.getTableColumnNames("users")).thenThrow(new SQLException());
        command.execute();
        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n" +
                ", Failure, because null]");
    }

    @Test
    public void testTableCreateRecordWithSQLException2() throws Exception {
        //given
        DataSet dataSet = new DataSetImpl();
        Set<String> columnNames = new LinkedHashSet<>();
        columnNames.add("id");
        columnNames.add("names");
        columnNames.add("password");
        //when
        when(runParameters.getTableName()).thenReturn("users");
        when(manager.getTableColumnNames("users")).thenReturn(columnNames);
        when(view.readLine()).thenReturn("id|1|names|Some Name|password|somepassword");
        doThrow(new SQLException()).when(manager).createTableRecord(anyString(), any(DataSet.class));
        command.execute();
        //then
        shouldPrint("[Enter data to create table record.\n" +
                "Input format: ColumnName1|Value1|ColumnName2|Value2| ... |ColumnNameN|ValueN\n" +
                ", Available column name for table <users> :, [id, names, password], Failure, because null]");
    }

    private void shouldPrint(String expected) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(view, atLeastOnce()).writeMessage(captor.capture());
        assertEquals(expected, captor.getAllValues().toString());
    }
}
