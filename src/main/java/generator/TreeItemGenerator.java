package generator;

/**
 * @author Leonhard Gahr
 */

import javafx.scene.control.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeItemGenerator {
    private Element root;
    private TreeItem<String> rootNode;
    private Document jdomDocument;
    private Map<TreeItem, Element> elementList = new HashMap<>();

    public TreeItemGenerator(final String XMLFilePath) throws Exception {
        jdomDocument = new SAXBuilder().build(XMLFilePath);
        root = jdomDocument.getRootElement();
    }

    /**
     * Sets the table items based on the xml-file
     */
    public void setTableItems() {
        rootNode = new TreeItem<>(getNamespace(root) + root.getName());

        elementList.put(rootNode, root);

        get(root, rootNode);
    }

    /**
     * Adds all children of a XML-Element to a TreeItem recursively
     *
     * @param node    The current node (initialize with root element)
     * @param current The current TreeItem (initialize with root item)
     */
    private void get(final Element node, final TreeItem<String> current) {
        current.setExpanded(true);

        // iterate through every child in current node
        node.getChildren().forEach(childNode -> {
            final TreeItem<String> item = new TreeItem<>(getNamespace(childNode) + childNode.getName());

            elementList.put(item, childNode);

            // recursive step
            get(childNode, item);

            // add node
            current.getChildren().add(item);
        });
    }

    /**
     * Execute a xPath and return a new root tree item
     *
     * @param xPath the xPath to be executed
     * @return The new tree root that contains the results
     */
    public TreeItem<String> executeXPath(final String xPath) {
        // TODO probably remove this line
        elementList.clear();
        final TreeItem<String> newRoot = new TreeItem<>();

        // catch case if xPath is just / to show the whole document
        if (xPath.equals("/")) {
            setTableItems();
            newRoot.getChildren().add(rootNode);
            return newRoot;
        }

        // interpretate xPath
        final XPathFactory xFactory = XPathFactory.instance();
        final XPathExpression<Element> expr = xFactory.compile(xPath, Filters.element(), null, root.getNamespace());
        final List<Element> results = expr.evaluate(jdomDocument);

        // iterate the results and add the content to the new root
        results.forEach(resultElement -> {
            final TreeItem<String> item = new TreeItem<>(getNamespace(resultElement) + resultElement.getName());
            elementList.put(item, resultElement);
            get(resultElement, item);
            newRoot.getChildren().add(item);
        });
        return newRoot;
    }

    public Element getRoot() {
        return root;
    }

    public TreeItem<String> getRootNode() {
        return rootNode;
    }

    public Map<TreeItem, Element> getElementList() {
        return elementList;
    }

    /**
     * Get the namespace prefix of a element with a ":" afterwards
     *
     * @param element The Element to get the namespace from
     * @return the namespace string (Example: "ns:")
     */
    public static String getNamespace(final Element element) {
        String ns = "";
        if (!element.getNamespace().getPrefix().equals("")) {
            ns = element.getNamespace().getPrefix() + ":";
        }
        return ns;
    }

    /**
     * get the XML tree as String to store it in a file recursively
     *
     * @param element The current element (on method call the init element)
     * @param level   The level for tabulators for the Einrückung
     * @return The XML string
     */
    // TODO change "Einrückung"
    private String getXMLString(final Element element, final int level) {
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
    private boolean hasContent(final Element element) {
        return element.getContent().size() != 0;
    }

    /**
     * Get the attributes of an element as XML string
     *
     * @param element The Element to get the attributes from
     * @return The complete String
     */
    private String getAttributeStrings(final Element element) {
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
     * Store TreeElements as
     *
     * @param elements
     * @param path
     * @throws IOException
     */
    public void saveXML(final List<TreeItem<String>> elements, final String path) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(path));

        elements.forEach(e -> {
            try {
                bw.write(getXMLString(elementList.get(e), 0) + "\n");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }
}
