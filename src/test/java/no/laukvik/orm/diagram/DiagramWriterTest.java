package no.laukvik.orm.diagram;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagramWriterTest {

    @Test
    void writeFile() throws IOException {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", "INT", "The internal id"));
        columns.add(new Column("email", "VARHAR(50)", "The email address"));
        columns.add(new Column("password", "VARHAR(30)", "The password"));
        columns.add(new Column("type", "INT", "The type of account"));
        Diagram diagram = new Diagram("User", "User account settings", columns);
        DiagramWriter writer = new DiagramWriter();
        File file = File.createTempFile("DiagramWriterTest", ".html");
        writer.writeFile( file, diagram);
        System.out.println(file.getAbsolutePath());
    }
}