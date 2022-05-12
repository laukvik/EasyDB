package no.laukvik.orm.diagram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DiagramWriter {

    public DiagramWriter() {
    }

    public void writeFile(File file, Diagram... diagrams) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(toHtml(diagrams));
        }
    }

    String toHtml(Diagram... diagrams){
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<style>");
        builder.append(getStyles());
        builder.append("</style>");
        builder.append("</head>");
        builder.append("<body>");
        for (Diagram d : diagrams){
            builder.append(toHtml(d));
        }
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }

    static String toHtml(Diagram diagram){
        StringBuilder builder = new StringBuilder();

        builder.append("<div class=\"orm\">");
        builder.append("<table>");

        builder.append("<thead>");
        builder.append("<tr>");
        builder.append("<th colspan=\"3\">");
        builder.append(diagram.name);
        builder.append("</th>");
        builder.append("</tr>");
        builder.append("</thead>");

        for (Column column : diagram.columns){
            builder.append("<tbody>");
            builder.append("<tr>");
            builder.append("<td>");
            builder.append(column.name);
            builder.append("</td>");
            builder.append("<td>");
            builder.append(column.type);
            builder.append("</td>");
            builder.append("<td>");
            builder.append(column.description);
            builder.append("</td>");
            builder.append("</tr>");
            builder.append("</tbody>");
        }

        builder.append("<tfoot>");
        builder.append("<tr>");
        builder.append("<td colspan=\"3\">");
        builder.append(diagram.description);
        builder.append("</td>");
        builder.append("</tr>");
        builder.append("</tfoot>");

        builder.append("</table>");
        builder.append("</div>");

        return builder.toString();
    }

    public String getStyles(){
        return ".orm {\n" +
                "    background-color: #eee;\n" +
                "    border-radius: 10px;\n" +
                "    border: 1px solid #ccc;\n" +
                "    font-family: sans-serif;\n" +
                "    padding: 0.5rem;\n" +
                "    width: 30rem;\n" +
                "}\n" +
                ".orm table{\n" +
                "    width: 100%;\n" +
                "    border-collapse: collapse;\n" +
                "}\n" +
                ".orm tbody TD{\n" +
                "    background-color: #ddd;\n" +
                "    border: 1px solid #aaa;\n" +
                "}\n" +
                ".orm tbody tr {\n" +
                "    margin: 2rem;\n" +
                "    padding: 2rem;\n" +
                "    background-color: aquamarine;\n" +
                "}\n" +
                ".orm td, .orm th {\n" +
                "    padding: 0.4rem;\n" +
                "}\n" +
                ".orm tfoot td {\n" +
                "    padding-top: 1rem;\n" +
                "    font-size: small;\n" +
                "    font-weight: normal;\n" +
                "    opacity: 0.5;\n" +
                "}";
    }


}
