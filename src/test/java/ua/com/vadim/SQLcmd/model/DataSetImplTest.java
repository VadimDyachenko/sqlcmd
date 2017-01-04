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
        //given
        String name = "Name";
        String value = "TestName";
        //when
        dataSet.put(name, value );
        String result = dataSet.get(name).toString();
        //then
        assertEquals(value, result);
    }

    @Test
    public void testGetNames() {
        //when
        Set<String> resultKeySet = dataSet.getNames();

        //then
        assertEquals("[id, name, password]", resultKeySet.toString());
    }

    @Test
    public void testGetValues() {
        //when
        List<Object> resultList = dataSet.getValues();

        //then
        assertEquals("[1, TestName, qwerty]", resultList.toString());
    }

    @Test
    public void testUpdateFrom() {
        //given
        DataSet newDataSet = new DataSetImpl();
        newDataSet.put("name", "NewName");

        //when
        dataSet.updateFrom(newDataSet);
        List<Object> resultList = dataSet.getValues();

        //then
        assertEquals("[1, NewName, qwerty]", resultList.toString());
    }

    @Test
    public void testToString() {
        //when
        String actualResult = dataSet.toString();

        //then
        assertEquals("{id:1, name:TestName, password:qwerty}", actualResult);
    }
}
