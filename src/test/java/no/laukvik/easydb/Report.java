package no.laukvik.easydb;

@DatabaseReport(query="select firstname from employee where IS_ACTIVE=true")
public class Report {

    @StringValue(column="FIRST_NAME")
    String first;

}
