package no.laukvik.easydb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Model {
    String table();

    String id() default "id";

    boolean auto() default true;
}