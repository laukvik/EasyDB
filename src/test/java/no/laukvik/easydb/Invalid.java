package no.laukvik.easydb;

@Model(table="invalid")
public class Invalid {

    @IntegerValue(column="EMPLOYEE_NR")
    String employeeNr;

}
