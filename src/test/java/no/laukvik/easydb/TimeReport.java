package no.laukvik.easydb;

@Report(query="select FIRST_NAME from employee where IS_ACTIVE=true")
public class TimeReport {

    @StringValue(column="FIRST_NAME")
    String first;

}
