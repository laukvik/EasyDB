package no.laukvik.easydb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    EnumType type() default EnumType.Integer;

    String column();

    int size() default 20;
}

enum EnumType {
    String, Integer
}