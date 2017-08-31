package generator;

import javafx.scene.control.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

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
}
