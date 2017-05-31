package XMLParser;

/**
 * @author Leonhard Gahr
 */

import javafx.scene.control.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class Parser {
    private Element root;
    private Document jdomDocument;
    private TreeItem<String> rootNode;

    public Parser(String XMLFilePath) throws Exception {
        jdomDocument = new SAXBuilder().build(XMLFilePath);
    }

    /**
     * Sets the table items based on the xml-file
     */
    public void setTableItems() {
        root = jdomDocument.getRootElement();
        rootNode = new TreeItem<String>(root.getName());

        if (!root.getTextTrim().equals("")) {
            rootNode = new TreeItem<String>(root.getName() + " (\"" + root.getTextTrim() + "\")");
        } else {
            rootNode = new TreeItem<String>(root.getName());
        }

        get(root, rootNode);
    }

    /**
     * Adds all children of a XML-Element to a TreeItem recursively
     *
     * @param node The current node (initialize with root element)
     * @param current The current TreeItem (initialize with root item)
     */
    private void get(Element node, TreeItem<String> current) {
        current.setExpanded(true);

        // iterate through every child in current node
        for (Element childNode : node.getChildren()) {
            TreeItem<String> item;
            // handle if the current node has a textvalue
            if (!childNode.getTextTrim().equals("")) {
                item = new TreeItem<String>(childNode.getName() + " (\"" + childNode.getTextTrim() + "\")");
            } else {
                item = new TreeItem<String>(childNode.getName());
            }
            // recursive step
            get(childNode, item);

            // add node
            current.getChildren().add(item);
        }
    }

    public Element getRoot() {
        return root;
    }

    public Document getJdomDocument() {
        return jdomDocument;
    }

    public TreeItem<String> getRootNode() {
        return rootNode;
    }
}
