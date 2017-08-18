package gui;

import generator.TreeItemGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leonhard.Gahr on 31/05/2017
 */

public class Controller {
    @FXML
    private TreeView<String> tree;

    @FXML
    private TextArea elementText;

    @FXML
    private Label breadcrump;

    @FXML
    private TextField xPathExpression;

    @FXML
    private TableView attributeTable;

    @FXML
    private TableColumn col1;

    @FXML
    private TableColumn col2;


    private TreeItemGenerator treeItemGenerator;

    @FXML
    void initialize() throws Exception {
        openXML();
    }

    public void executeXPath() {
        tree.setRoot(treeItemGenerator.executeXPath(xPathExpression.getText()));
    }

    private String generateBreadcrump(Element element) {
        String ns = "";
        if (!element.getNamespace().getPrefix().equals("")) {
            ns = element.getNamespace().getPrefix() + ":";
        }
        if (element.getParentElement() != null) {
            return generateBreadcrump(element.getParentElement()) + "/" + ns + element.getName();
        } else {
            return "/" + ns + element.getName();
        }
    }

    @FXML
    private void updateInfo() {
        TreeItem<String> currentItem = (tree.getFocusModel().getFocusedItem());
        Element currentElement = treeItemGenerator.getElementList().get(tree.getRow(currentItem));

        addRow(attributeTable);


        breadcrump.setText(generateBreadcrump(currentElement));

        elementText.setText(currentElement.getText());
    }

    private void addRow(TableView table) {
        // TODO: 18/08/2017 Add data to table
    }

    public void openXML() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        String file;
        try {
            file = fileChooser.showOpenDialog(new Stage()).getAbsolutePath();
        } catch (NullPointerException e) {
            return;
        }

        TreeItem<String> root = new TreeItem<String>();

        treeItemGenerator = new TreeItemGenerator(file);

        treeItemGenerator.setTableItems();

        root.getChildren().add(treeItemGenerator.getRootNode());

        tree.setRoot(root);

        tree.setShowRoot(false);
    }

    public void saveXML() throws IOException, JDOMException {
        List<Element> elementList = new ArrayList<Element>();
        elementList.add(treeItemGenerator.getRoot());
        treeItemGenerator.saveXML(elementList);
    }
}
