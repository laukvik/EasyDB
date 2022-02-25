package no.laukvik.orm.diagram;

import java.util.List;

public class Diagram {

    String name;
    String description;
    List<Column> columns;

    public Diagram(String name, String description, List<Column> columns) {
        this.name = name;
        this.description = description;
        this.columns = columns;
    }
}
