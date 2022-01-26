package no.laukvik.orm.diagram;

public class Column {

    String name;
    String type;
    String description;

    public Column(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
}
