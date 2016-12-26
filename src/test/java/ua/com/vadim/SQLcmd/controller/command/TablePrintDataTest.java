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
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TablePrintDataTest {
    private static RunParameters runParameters;
    private static ResourceBundle resources;
    private DatabaseManager manager;
    private View view;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        runParameters = new PropertiesLoader().getParameters();
        resources = ResourceBundle.getBundle("TablePrintData", new UTF8Control());
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TablePrintData(runParameters, manager, view);
    }

    @Test
    public void testTableIsEmpty() throws SQLException {
        //given
        DataSet[] tableData = new DataSetImpl[0];
        String tableName = "tableA";
        runParameters.setTableName(tableName);
        String messageFormatted = String.format(resources.getString("table.print.data.empty"), tableName);
        String expectedMessage = String.format("[%s]", messageFormatted);
        //when
        when(manager.getTableData(tableName)).thenReturn(tableData);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTablePrintData() throws SQLException {
        //given
        DataSet[] tableData = new DataSetImpl[1];
        DataSet dataSet = new DataSetImpl();
        dataSet.put("id", 1);
        dataSet.put("name", "TestName");
        dataSet.put("password", "qwerty");
        tableData[0] = dataSet;
        String tableName = "tableA";
        runParameters.setTableName(tableName);
        String expectedMessage = "[|id|name    |password|, " +
                                  "+--+--------+--------+, " +
                                  "|1 |TestName|qwerty  |]";
        //when
        when(manager.getTableData(tableName)).thenReturn(tableData);
        command.execute();
        //then
        shouldPrint(expectedMessage);
    }

    @Test
    public void testTablePrintDataWithSQLException() throws SQLException {
        //given
        String tableName = "tableA";
        runParameters.setTableName(tableName);
        String exceptionMessage = "Some SQLException";
        String expectedMessage = String.format("[%s%s]",
                resources.getString("table.print.data.failed"),
                exceptionMessage);
        //when
        when(manager.getTableData(tableName)).thenThrow(new SQLException(exceptionMessage));
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
