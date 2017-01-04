package ua.com.vadim.SQLcmd.controller.command;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ua.com.vadim.SQLcmd.controller.PropertiesLoader;
import ua.com.vadim.SQLcmd.controller.RunParameters;
import ua.com.vadim.SQLcmd.exception.ExitException;
import ua.com.vadim.SQLcmd.model.DataSet;
import ua.com.vadim.SQLcmd.model.DataSetImpl;
import ua.com.vadim.SQLcmd.model.DatabaseManager;
import ua.com.vadim.SQLcmd.view.View;

import java.sql.SQLException;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class TablePrintDataTest extends AbstractCommandTest{
    private static RunParameters parameters;
    private View view;
    private DatabaseManager manager;
    private Command command;

    @BeforeClass
    public static void beforeAllTestSetUp() throws ExitException {
        Locale.setDefault(Locale.ENGLISH);
        parameters = new PropertiesLoader().getParameters();
    }

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        command = new TablePrintData(parameters, manager, view);
    }

    @Test
    public void testTableIsEmpty() throws SQLException, ExitException {
        //given
        DataSet[] tableData = new DataSetImpl[0];
        String tableName = "tableA";
        parameters.setTableName(tableName);
        when(manager.getTableData(tableName)).thenReturn(tableData);

        //when
        command.execute();

        //then
        shouldPrint("[Table <tableA> is empty.]");
    }

    @Test
    public void testTablePrintData() throws SQLException, ExitException {
        //given
        DataSet[] tableData = new DataSetImpl[1];
        DataSet dataSet = new DataSetImpl();
        dataSet.put("id", 1);
        dataSet.put("name", "TestName");
        dataSet.put("password", "qwerty");
        tableData[0] = dataSet;

        String tableName = "tableA";
        parameters.setTableName(tableName);
        when(manager.getTableData(tableName)).thenReturn(tableData);

        //when
        command.execute();

        //then
        shouldPrint("[|id|name    |password|, +--+--------+--------+, |1 |TestName|qwerty  |]");
    }

    @Test
    public void testTablePrintDataWithSQLException() throws SQLException, ExitException {
        //given
        String tableName = "tableA";
        parameters.setTableName(tableName);

        when(manager.getTableData(tableName)).thenThrow(new SQLException("Some SQLException"));
        //when

        command.execute();
        //then
        shouldPrint("[Failure because:Some SQLException]");
    }

    @Override
    View getView() {
        return view;
    }
}
