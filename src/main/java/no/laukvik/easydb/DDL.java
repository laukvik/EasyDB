package no.laukvik.easydb;

import java.lang.reflect.Field;
import java.util.HashMap;

import static no.laukvik.easydb.EasyDB.getModel;

class DDL {

    static String deleteTable(Class klass) {
        Model model = getModel(klass);
        String table = model.table();
        return "DROP TABLE " + table;
    }

    static String createTable(Class klass) {
        Model model = getModel(klass);
        String table = model.table();
        HashMap<String, Field> map = Mapper.extractFields(klass);
        StringBuffer buffer = new StringBuffer(4000);
        int index = 0;
        for (String column : map.keySet()) {
            if (index > 0) {
                buffer.append(", ");
            }
            buffer.append(get(map.get(column), model.auto() && column.equalsIgnoreCase(model.id())));
            index++;
        }
        buffer.append(", PRIMARY KEY(" + model.id() + ")");
        return "CREATE TABLE " + table + " (" + buffer + ")";
    }

    static String get(Field f, boolean auto) {
        if (f.isAnnotationPresent(IntegerValue.class)) {
            return getInteger(f.getAnnotation(IntegerValue.class), auto);
        }
        if (f.isAnnotationPresent(FloatValue.class)) {
            return getFloat(f.getAnnotation(FloatValue.class));
        }
        if (f.isAnnotationPresent(StringValue.class)) {
            return getString(f.getAnnotation(StringValue.class));
        }
        if (f.isAnnotationPresent(EnumValue.class)) {
            return getEnum(f.getAnnotation(EnumValue.class));
        }
        if (f.isAnnotationPresent(BooleanValue.class)) {
            return getBoolean(f.getAnnotation(BooleanValue.class));
        }
        if (f.isAnnotationPresent(DateValue.class)) {
            return getDate(f.getAnnotation(DateValue.class));
        }
        if (f.isAnnotationPresent(TimestampValue.class)) {
            return getTimestampValue(f.getAnnotation(TimestampValue.class));
        }
        return "";
    }

    static String getTimestampValue(TimestampValue value) {
        return value.column() + " TIMESTAMP";
    }

    static String getDate(DateValue value) {
        return value.column() + " DATE";
    }

    static String getBoolean(BooleanValue value) {
        return value.column() + " BOOLEAN";
    }

    static String getEnum(EnumValue value) {
        return value.column() + " VARCHAR(" + value.size() + ")";
    }

    static String getInteger(IntegerValue value, boolean auto) {
        if (auto) {
            return value.column() + " INT NOT NULL GENERATED ALWAYS AS IDENTITY";
        }
        return value.column() + " INT";
    }

    static String getFloat(FloatValue value) {
        return value.column() + " REAL";
    }

    static String getString(StringValue value) {
        if (value.size() < 0) {
            throw new IllegalStateException("Size must be positive");
        }
        if (value.size() > 0) {
            return value.column() + " VARCHAR(" + value.size() + ")";
        }
        return value.column() + " TEXT";
    }

}
