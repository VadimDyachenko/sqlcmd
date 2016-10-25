package ua.vadim.sqlcmd.model;

import java.util.List;
import java.util.Set;

public interface DataSet {
    void put(String name, Object value);

    List<Object> getValues();

    Set<String> getNames();

    Object get(String name);

    void updateFrom(DataSet newValue);
}
