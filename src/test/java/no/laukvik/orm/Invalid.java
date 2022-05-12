package no.laukvik.orm;

@Model(table="invalid")
public class Invalid {

    @IntegerValue(column="EMPLOYEE_NR")
    String employeeNr;

}
