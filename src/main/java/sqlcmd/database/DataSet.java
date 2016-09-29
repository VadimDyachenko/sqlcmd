package sqlcmd.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSet {
    static class Data {
        private String name;
        private Object value;

        public Data(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    private List<Data> data = new ArrayList<Data>();

    public void put(String name, Object value) {
        data.add(new Data(name, value));
    }

    public Object[] getValues() {
        Object[] result = new Object[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i).getValue();
        }
        return result;
    }

    public String[] getNames() {
        String[] result = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i).getName();
        }
        return result;
    }

    @Override
    public String toString() {
        return "DataSet{\n" +
                "names:" + Arrays.toString(getNames()) + "\n" +
                "values:" + Arrays.toString(getValues()) + "\n" +
                "}";
    }
}
