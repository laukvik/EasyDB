package no.laukvik.orm;

@Report(query="select FIRST_NAME from employee where IS_ACTIVE=?",item=TimeReport.Item.class)
public class TimeReport {

    @ReportParam(index=1)
    Boolean active;

    public static class Item {
        @StringValue(column="FIRST_NAME")
        String first;

        public Item(){
        }

    }
}
