package no.laukvik.easydb;

import java.time.LocalDate;
import java.util.Date;

/**

CREATE TABLE EMPLOYEE (
    ID INT NOT NULL,
    EMPLOYEE_NR INT,
    hired DATE,
    IS_ACTIVE BOOLEAN,
    created TIMESTAMP,
    EMPLOYEE_TYPE INT,
    FIRST_NAME VARCHAR(50)
)

*/

// https://www.postgresql.org/docs/9.5/datatype.html
@DatabaseModel(table="EMPLOYEE", id="ID", auto=true)
public class Employee {

    @IntegerValue(column = "ID")
    Integer id;

    @FloatValue(column = "CASH")
    Float cash;

    @IntegerValue(column="EMPLOYEE_NR")
    Integer employeeNr;

    @StringValue(column="FIRST_NAME", size=20)
    String first;

    @BooleanValue(column="IS_ACTIVE")
    boolean active;

    @DateValue(column="hired")
    LocalDate hired;

    @TimestampValue(column="created")
    Date created;

    @EnumValue(column="EMPLOYEE_TYPE")
    EmployeeType type;

}

enum EmployeeType {
    Unknown, Boss, Normal, Genious
}

