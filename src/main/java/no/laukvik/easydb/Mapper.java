package no.laukvik.easydb;

import no.laukvik.easydb.exception.MappingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

public class Mapper {

    public static HashMap<String, Field> extractFields(Class klass) {
        HashMap<String, Field> map = new HashMap<>();
        for (Field f : klass.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.isAnnotationPresent(FloatValue.class)) {
                if (f.getType() != Float.class) {
                    throw new MappingException("FloatValue must be mapped to an Float field");
                }
                map.put(f.getAnnotation(FloatValue.class).column(), f);
            }
            if (f.isAnnotationPresent(StringValue.class)) {
                if (f.getType() != String.class) {
                    throw new MappingException("StringColumn must be mapped to a String field");
                }
                map.put(f.getAnnotation(StringValue.class).column(), f);
            }
            if (f.isAnnotationPresent(IntegerValue.class)) {
                if (f.getType() != Integer.class) {
                    throw new MappingException("NumberColumn must be mapped to a Number field. (Was " + f.getType() + ")");
                }
                map.put(f.getAnnotation(IntegerValue.class).column(), f);
            }
            if (f.isAnnotationPresent(BooleanValue.class)) {
                if (!(f.getType() == boolean.class || f.getType() == Boolean.class)) {
                    throw new MappingException("BooleanColumn must be mapped to a Boolean field. (Was " + f.getType() + ")");
                }
                map.put(f.getAnnotation(BooleanValue.class).column(), f);
            }
            if (f.isAnnotationPresent(DateValue.class)) {
                if (!(f.getType() == Date.class || f.getType() == LocalDate.class)) {
                    throw new MappingException("DateColumn must be mapped to an Date or LocalDate field. (Was " + f.getType() + ")");
                }
                map.put(f.getAnnotation(DateValue.class).column(), f);
            }
            if (f.isAnnotationPresent(TimestampValue.class)) {
                if (!(f.getType() == Date.class || f.getType() == LocalDateTime.class)) {
                    throw new MappingException("TimestampColumn must be mapped to a Date or LocalDateTime field. (Was " + f.getType() + ")");
                }
                map.put(f.getAnnotation(TimestampValue.class).column(), f);
            }
            if (f.isAnnotationPresent(EnumValue.class)) {
                if (!f.getType().isEnum()) {
                    throw new MappingException("EnumColumn must be mapped to an enum field. (Was " + f.getType() + ")");
                }
                map.put(f.getAnnotation(EnumValue.class).column(), f);
            }
        }
        return map;
    }

    public static HashMap<String, Object> extractData(Object object) {
        HashMap<String, Object> map = new HashMap<>();
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.isAnnotationPresent(StringValue.class)) {
                    Annotation a = f.getAnnotation(StringValue.class);
                    StringValue column = (StringValue) a;
                    map.put(column.column(), f.get(object));
                }
                if (f.isAnnotationPresent(IntegerValue.class)) {
                    Annotation a = f.getAnnotation(IntegerValue.class);
                    IntegerValue column = (IntegerValue) a;
                    map.put(column.column(), f.get(object));
                }
                if (f.isAnnotationPresent(BooleanValue.class)) {
                    Annotation a = f.getAnnotation(BooleanValue.class);
                    BooleanValue column = (BooleanValue) a;
                    map.put(column.column(), f.get(object));
                }
                if (f.isAnnotationPresent(DateValue.class)) {
                    Annotation a = f.getAnnotation(DateValue.class);
                    DateValue column = (DateValue) a;
                    map.put(column.column(), f.get(object));
                }
                if (f.isAnnotationPresent(TimestampValue.class)) {
                    Annotation a = f.getAnnotation(TimestampValue.class);
                    TimestampValue column = (TimestampValue) a;
                    map.put(column.column(), f.get(object));
                }
                if (f.isAnnotationPresent(EnumValue.class)) {
                    Annotation a = f.getAnnotation(EnumValue.class);
                    EnumValue column = (EnumValue) a;
                    map.put(column.column(), f.get(object));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
