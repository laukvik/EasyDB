package no.laukvik.easydb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanValue {
    BooleanType type() default BooleanType.Integer;

    String column();
}

enum BooleanType {
    String, Char, Integer
}