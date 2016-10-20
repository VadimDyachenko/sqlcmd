package sqlcmd.controller.command;

import org.junit.Before;
import sqlcmd.controller.ConnectionStatusHelper;
import sqlcmd.model.DatabaseManager;
import sqlcmd.view.View;

import static org.mockito.Mockito.mock;

public class TableClearTest {
    private DatabaseManager manager;
    private View view;
    private Command command;

    @Before
    public void setUp() throws Exception {
        manager = mock(DatabaseManager.class);
        view = mock(View.class);
        ConnectionStatusHelper connectionStatusHelper = mock(ConnectionStatusHelper.class);
        command = new DBSelectTable(connectionStatusHelper, manager, view);
    }
}
