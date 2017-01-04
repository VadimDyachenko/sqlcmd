package ua.com.vadim.SQLcmd.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DataSetImplTest {
    private DataSet dataSet;

    @Before
    public void setUp() {
        dataSet = new DataSetImpl();
        dataSet.put("id", 1);
        dataSet.put("name", "TestName");
        dataSet.put("password", "qwerty");
    }

    @Test
    public void testPutGet() {
        //when
        dataSet.put("Name", "TestName");
        String result = dataSet.get("Name").toString();

        //then
        assertEquals("TestName", result);
    }

    @Test
    public void testGetNames() {
        //when
        Set<String> names = dataSet.getNames();

        //then
        assertEquals("[id, name, password]", names.toString());
    }

    @Test
    public void testGetValues() {

        //then
        assertEquals("[1, TestName, qwerty]", dataSet.getValues().toString());
    }

    @Test
    public void testUpdateFrom() {
        //given
        DataSet data = new DataSetImpl();
        data.put("name", "NewName");

        //when
        dataSet.updateFrom(data);

        //then
        assertEquals("[1, NewName, qwerty]", dataSet.getValues().toString());
    }

    @Test
    public void testToString() {
        //when
        String result = dataSet.toString();

        //then
        assertEquals("{id:1, name:TestName, password:qwerty}", result);
    }
}
