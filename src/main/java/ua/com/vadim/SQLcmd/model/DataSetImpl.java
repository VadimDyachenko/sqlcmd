package ua.com.vadim.SQLcmd.model;

import java.util.*;

public class DataSetImpl implements DataSet {

    private final Map<String, Object> data = new LinkedHashMap<>();

    @Override
    public void put(String name, Object value) {
        data.put(name, value);
    }

    @Override
    public List<Object> getValues() {
        return new LinkedList<>(data.values());
    }

    @Override
    public Set<String> getNames() {
        return data.keySet();
    }

    @Override
    public Object get(String name) {
        return data.get(name);
    }

    @Override
    public void updateFrom(DataSet newValue) {
        Set<String> columns = newValue.getNames();
        for (String name : columns) {
            Object data = newValue.get(name);
            put(name, data);
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        Iterator<String> iterator = getNames().iterator();

        result.append('{');
        while (iterator.hasNext()) {
            String columns = iterator.next();
            result.append(columns).append(':').append(get(columns));

            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
        result.append('}');

        return result.toString();
    }
}
