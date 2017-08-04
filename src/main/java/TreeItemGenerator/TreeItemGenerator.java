package TreeItemGenerator;

/**
 * @author Leonhard Gahr
 */

import javafx.scene.control.TreeItem;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.util.ArrayList;
import java.util.List;

public class TreeItemGenerator {
    private Element root;
    private TreeItem<String> rootNode;
    private List<Element> elementList = new ArrayList<Element>();

    public TreeItemGenerator(String XMLFilePath) throws Exception {
        root = new SAXBuilder().build(XMLFilePath).getRootElement();
        elementList.add(root);
    }

    /**
     * Sets the table items based on the xml-file
     */
    public void setTableItems() {
        rootNode = new TreeItem<String>(root.getName());

        rootNode = new TreeItem<String>(root.getName());

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
            elementList.add(childNode);

            TreeItem<String> item;

            item = new TreeItem<String>(childNode.getName());

            // recursive step
            get(childNode, item);

            // add node
            current.getChildren().add(item);
        }
    }

    public Element getRoot() {
        return root;
    }

    public TreeItem<String> getRootNode() {
        return rootNode;
    }

    public List<Element> getElementList() {
        return elementList;
    }
}
