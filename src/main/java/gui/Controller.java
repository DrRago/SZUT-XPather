package gui;

import generator.TreeItemGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TableView<Map<String, String>> attributeTable;

    @FXML
    private TableColumn<Map, String> col1;

    @FXML
    private TableColumn<Map, String> col2;

    private String currentFile;

    private TreeItemGenerator treeItemGenerator;

    @FXML
    void initialize() throws Exception {
        openXML();

        // automatically update tableView cell width
        attributeTable.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (double) newValue / 2 - 1;
            col1.setMinWidth(width);
            col1.setPrefWidth(width);
            col2.setMinWidth(width);
            col2.setPrefWidth(width);
        });
    }

    public void executeXPath() {
        try {
            TreeItem<String> newRoot = treeItemGenerator.executeXPath(xPathExpression.getText());
            if (newRoot.getChildren().size() == 0) {
                newRoot.getChildren().add(new TreeItem<>("Nothing to show here"));
            }
            tree.setRoot(newRoot);
        } catch (RuntimeException e) {
            Alert xpathAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.CLOSE);
            xpathAlert.setHeaderText("XPath is not valid");
            xpathAlert.showAndWait();
        }
    }

    private String generateBreadcrump(Element element) {
        // get the breadcrump string recursively

        String ns = TreeItemGenerator.getNamespace(element);

        if (element.getParentElement() != null) {
            return generateBreadcrump(element.getParentElement()) + "/" + ns + element.getName();
        } else {
            return "/" + ns + element.getName();
        }
    }

    @FXML
    private void updateInfo() {
        // get the selected item as JDOM Element
        TreeItem<String> currentItem = tree.getFocusModel().getFocusedItem();
        Element currentElement = treeItemGenerator.getElementList().get(currentItem);

        if (currentElement != null) {
            // update the attribute tableView
            updateAttributes(currentElement);

            // update breadcrump
            breadcrump.setText(generateBreadcrump(currentElement));

            // update element content text
            elementText.setText(currentElement.getTextTrim());
        }
    }

    private ObservableList<Map<String, String>> generateDataInMap(Element element) {
        /*
            Generate the Attribute table in a HashMap datastructure
            one row is a Map with two strings (Map<String, String>)
            The rows are added to a ObservableList which is the datatype for the table items
         */
        ObservableList<Map<String, String>> allData = FXCollections.observableArrayList();
        for (Attribute attr : element.getAttributes()) {
            Map<String, String> dataRow = new HashMap<>();

            String value1 = TreeItemGenerator.getNamespace(element) + attr.getName();
            String value2 = attr.getValue();

            dataRow.put("Attribute", value1);
            dataRow.put("Value", value2);

            allData.add(dataRow);
        }
        return allData;
    }

    private void updateAttributes(Element element) {
        // set a factory for the cells
        col1.setCellValueFactory(new MapValueFactory<>("Attribute"));
        col2.setCellValueFactory(new MapValueFactory<>("Value"));


        // add items to the table
        attributeTable.setItems(generateDataInMap(element));
    }

    @FXML
    private void openXML() throws Exception {
        openXML(null);
    }

    private void openXML(String path) throws Exception {
        String file;

        if (path == null) {
            // open XML-File with FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-Files", "*.xml"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

            try {
                file = fileChooser.showOpenDialog(new Stage()).getAbsolutePath();
            } catch (NullPointerException e) {
                return;
            }
        } else {
            file = path;
        }

        currentFile = file;

        // initialize the treeView
        TreeItem<String> root = new TreeItem<>();

        treeItemGenerator = new TreeItemGenerator(file);

        treeItemGenerator.setTableItems();

        root.getChildren().add(treeItemGenerator.getRootNode());

        tree.setRoot(root);

        tree.setShowRoot(false);
    }

    public void saveXML() throws IOException, JDOMException {
        // get all children of root because of the invisible root
        List<TreeItem<String>> elementList = new ArrayList<>();
        elementList.addAll(tree.getRoot().getChildren());

        // set the file destination
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        treeItemGenerator.saveXML(elementList, fileChooser.showSaveDialog(new Stage()).getAbsolutePath());
    }

    public void resetFile() throws Exception {
        openXML(currentFile);
    }

    public void exitProgram() {
        System.exit(0);
    }
}
