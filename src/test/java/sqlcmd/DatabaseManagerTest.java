package sqlcmd;

import org.junit.Before;
import org.junit.Test;
import sqlcmd.DatabaseManager;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vadim on 24.09.2016.
 */
public class DatabaseManagerTest {
    private DatabaseManager manager;
    @Before
    public void setupManager() {
        manager = new DatabaseManager();
        manager.connect("sqlcmd", "javauser", "test");
    }

    @Test
    public void testGetAllTableNames() {
        String[] tableNames = manager.getAllTableNames();
        assertEquals("[users, staff]", Arrays.toString(tableNames));
        System.out.println("");
    }
}
