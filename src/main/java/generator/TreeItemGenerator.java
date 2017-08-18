package generator;

/**
 * @author Leonhard Gahr
 */

import javafx.scene.control.TreeItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TreeItemGenerator {
    private Element root;
    private TreeItem<String> rootNode;
    private Document jdomDocument;
    private List<Element> elementList = new ArrayList<Element>();

    public TreeItemGenerator(String XMLFilePath) throws Exception {
        jdomDocument = new SAXBuilder().build(XMLFilePath);
        root = jdomDocument.getRootElement();

        elementList.add(root);
    }

    /**
     * Sets the table items based on the xml-file
     */
    public void setTableItems() {
        rootNode = new TreeItem<String>(getNamespacePrefix(root) + root.getName());

        get(root, rootNode);
    }

    private String getNamespacePrefix(Element element) {
        String ns = "";
        if (!element.getNamespace().getPrefix().equals("")) {
            ns = element.getNamespace().getPrefix() + ":";
        }
        return ns;
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
            item = new TreeItem<String>(getNamespacePrefix(childNode) + childNode.getName());

            // recursive step
            get(childNode, item);

            // add node
            current.getChildren().add(item);
        }
    }

    public TreeItem<String> executeXPath(String xPath) {
        elementList.clear();
        TreeItem<String> newRoot = new TreeItem<String>();

        XPathFactory xFactory = XPathFactory.instance();

        XPathExpression<Element> expr = xFactory.compile(xPath, Filters.element(), null, root.getNamespace());
        List<Element> results = expr.evaluate(jdomDocument);
        for (Element resultElement : results) {
            elementList.add(resultElement);
            TreeItem<String> item = new TreeItem<String>(getNamespacePrefix(resultElement) + resultElement.getName());
            get(resultElement, item);
            newRoot.getChildren().add(item);
        }
        return newRoot;
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

    public void saveXML(List<Element> elements) throws IOException, JDOMException {
        Document document = new Document();
        Element root = new Element("fml");
        root.getContent().addAll(elements.get(0).getContent());
        document.setRootElement(root);

        document.setRootElement(elements.get(0));

        FileWriter writer = new FileWriter("generatedXmlFiles/listeCompte.xml");
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(document, writer);
        outputter.output(document, System.out);
        writer.close(); // close writer

    }
}
