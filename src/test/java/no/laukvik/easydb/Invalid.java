package no.laukvik.easydb;

@DatabaseModel(table="invalid")
public class Invalid {

    @IntegerValue(column="EMPLOYEE_NR")
    String employeeNr;

}
