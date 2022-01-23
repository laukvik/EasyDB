package no.laukvik.orm;

import java.time.LocalDate;
import java.util.Date;

@Model(table="EMPLOYEE", id="ID", auto=true)
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

