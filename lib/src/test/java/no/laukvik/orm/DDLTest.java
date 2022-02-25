package no.laukvik.orm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DDLTest {

    @Test
    void createTable() {
        assertEquals("CREATE TABLE EMPLOYEE (EMPLOYEE_TYPE VARCHAR(20), created TIMESTAMP, EMPLOYEE_NR INT, hired DATE, ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, CASH REAL, IS_ACTIVE BOOLEAN, FIRST_NAME VARCHAR(20), PRIMARY KEY(ID))", DDL.createTable(Employee.class));
    }

    @Test
    void get() {
    }

    @Test
    void getInteger() {
    }
}