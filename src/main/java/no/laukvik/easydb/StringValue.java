package no.laukvik.easydb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StringValue {
    //    boolean nullable();
    String column();

    int size() default 0;
}