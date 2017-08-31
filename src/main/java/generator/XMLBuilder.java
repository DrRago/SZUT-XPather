package generator;

import javafx.scene.control.TreeItem;
import org.jdom2.Element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static generator.TreeItemGenerator.getNamespace;

public class XMLBuilder {
    /**
     * get the XML tree as String to store it in a file recursively
     *
     * @param element The current element (on method call the init element)
     * @param level   The level for tabulators for the indent
     * @return The XML string
     */
    private static String getXMLString(final Element element, final int level) {
        final StringBuilder string = new StringBuilder();
        final StringBuilder tabulators = new StringBuilder();

        for (int i = 0; i < level; i++) {
            tabulators.append("\t");
        }

        // open the tag
        string.append(tabulators)
                .append("<")
                .append(getNamespace(element))
                .append(element.getName())
                .append(getAttributeStrings(element));

        // check if the tag should be closed immediately
        if (hasContent(element)) {
            string.append(">\n");

            // recursive step for all children of the element
            for (final Element child : element.getChildren()) {
                string.append(getXMLString(child, level + 1));
            }

            // close the tag
            string.append(tabulators)
                    .append("</")
                    .append(getNamespace(element))
                    .append(element.getName())
                    .append(">\n");
        } else {
            // close the tag
            string.append("/>\n");
        }

        return string.toString();
    }

    /**
     * Check if a Element has any type of content
     *
     * @param element The element to be checked
     * @return True if it has content, False if it hasn't
     */
    private static boolean hasContent(final Element element) {
        return element.getContent().size() != 0;
    }

    /**
     * Get the attributes of an element as XML string
     *
     * @param element The Element to get the attributes from
     * @return The complete String
     */
    private static String getAttributeStrings(final Element element) {
        final StringBuilder attributes = new StringBuilder();

        // Build the string for each attribute
        element.getAttributes().forEach(
                attribute -> attributes.append(" ")
                        .append(getNamespace(element))
                        .append(attribute.getName())
                        .append("=\"")
                        .append(attribute.getValue())
                        .append("\"")
        );

        return attributes.toString();
    }

    /**
     * Store TreeElements as XML
     *
     * @param elements List of elements to write to file
     * @param path The path of the destination file
     * @throws IOException On Write error
     */
    public static void saveXML(final List<TreeItem<String>> elements, final String path, TreeItemGenerator treeItemGenerator) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(path));

        elements.forEach(e -> {
            try {
                bw.write(getXMLString(treeItemGenerator.getElementList().get(e), 0) + "\n");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        bw.close();
    }
}
