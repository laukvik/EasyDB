package no.laukvik.orm;

public interface JsonMapper {
    String toJson(Object value);
    Object toObject(String value);
}
