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
        //given
        Set<String> resultKeySet;
        String expectedResult = "[id, name, password]";
        //when
        resultKeySet = dataSet.getNames();
        //then
        assertEquals(expectedResult, resultKeySet.toString());
    }

    @Test
    public void testGetValues() {
        //given
        List<Object> resultList;
        String expectedResult = "[1, TestName, qwerty]";
        //when
        resultList = dataSet.getValues();
        //then
        assertEquals(expectedResult, resultList.toString());
    }

    @Test
    public void testUpdateFrom() {
        //given
        DataSet newDataSet = new DataSetImpl();
        newDataSet.put("name", "NewName");
        String expectedResult = "[1, NewName, qwerty]";
        //when
        dataSet.updateFrom(newDataSet);
        List<Object> resultList = dataSet.getValues();
        //then
        assertEquals(expectedResult,resultList.toString());
    }

    @Test
    public void testToString() {
        //given
        String expectedResult = "{id:1, name:TestName, password:qwerty}";
        //when
        String actualResult = dataSet.toString();
        //then
        assertEquals(expectedResult,actualResult);
    }
}
