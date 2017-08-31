package gui;

import generator.TreeItemGenerator;
import generator.XMLBuilder;
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
import org.jdom2.input.JDOMParseException;

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

    /**
     * Initialize the gui
     * @throws JDOMException Inherited from openXML
     * @throws IOException Inherited from openXML
     */
    @FXML
    void initialize() throws JDOMException, IOException {
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

    /**
     * Execute a xPath
     */
    @FXML
    private void executeXPath() {
        try {
            // Pass XPath to treeItemGenerator and add the output to the treeView
            TreeItem<String> newRoot = treeItemGenerator.executeXPath(xPathExpression.getText());
            if (newRoot.getChildren().size() == 0) {
                newRoot.getChildren().add(new TreeItem<>("Nothing to show here"));
            }
            tree.setRoot(newRoot);
        } catch (RuntimeException e) {
            // catch a XPath error and display a error message
            Alert xpathAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.CLOSE);
            xpathAlert.setHeaderText("XPath is not valid");
            xpathAlert.showAndWait();
        }
    }

    /**
     * Event on selection change in treeView
     */
    @FXML
    private void updateInfo() {
        // get the selected item as JDOM Element
        TreeItem<String> currentItem = tree.getFocusModel().getFocusedItem();
        Element currentElement = treeItemGenerator.getElementList().get(currentItem);

        if (currentElement != null) {
            // update the attribute tableView
            updateAttributes(currentElement);

            // update breadcrumb
            breadcrump.setText(generateBreadcrumb(currentElement));

            // update element content text
            elementText.setText(currentElement.getTextTrim());
        }
    }
    @FXML
    private void openXML() throws JDOMException, IOException {
        openXML(null);
    }

    @FXML
    private void resetFile() throws JDOMException, IOException {
        openXML(currentFile);
    }

    @FXML
    public void exitProgram() {
        System.exit(0);
    }

    /**
     * Generate the breadcrumb string recursively
     *
     * @param element The element to get the breadcrumb from
     * @return a XPath string
     */
    private String generateBreadcrumb(Element element) {
        // get the breadcrumb string recursively

        String ns = TreeItemGenerator.getNamespace(element);

        if (element.getParentElement() != null) {
            return generateBreadcrumb(element.getParentElement()) + "/" + ns + element.getName();
        } else {
            return "/" + ns + element.getName();
        }
    }

    /**
     * Generate the Attribute table in a HashMap datastructure
     * one row is a Map with two strings (Map<String, String>)
     * The rows are added to a ObservableList which is the datatype for the table items
     *
     * @param element The element that we get the data from
     * @return A list to set as table items
     */
    private ObservableList<Map<String, String>> generateDataInMap(Element element) {
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

    /**
     * Update the attribute table
     *
     * @param element The element to get the attributes from
     */
    private void updateAttributes(Element element) {
        // set a factory for the cells
        col1.setCellValueFactory(new MapValueFactory<>("Attribute"));
        col2.setCellValueFactory(new MapValueFactory<>("Value"));

        // add items to the table
        attributeTable.setItems(generateDataInMap(element));
    }

    /**
     * Open a XML file and load it into the gui
     *
     * @param path (Optional) the Path of the XML file, if null, a fileOpenDialog will be opened
     * @throws JDOMException inherited from TreeItemGenerator
     * @throws IOException inherited from TreeItemGenerator
     */
    private void openXML(String path) throws JDOMException, IOException {
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
                // show alert to select a file if no file is currently active
                if (currentFile == null) {
                    Alert noFileAlert = new Alert(Alert.AlertType.INFORMATION, "Please select a file. The program will not work without a XML-File", ButtonType.CLOSE);
                    noFileAlert.setHeaderText("No file selected");
                    noFileAlert.showAndWait();
                    openXML();
                }
                return;
            }
        } else {
            file = path;
        }

        currentFile = file;

        // initialize the treeView
        TreeItem<String> root = new TreeItem<>();

        try {
            treeItemGenerator = new TreeItemGenerator(file);

            treeItemGenerator.setTableItems();

            root.getChildren().add(treeItemGenerator.getRootNode());

            tree.setRoot(root);

            tree.setShowRoot(false);
        } catch (JDOMParseException e) {
            Alert exceptionAlert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            exceptionAlert.setHeaderText(e.getSystemId());
            exceptionAlert.showAndWait();

            // open another file on fail
            openXML();
        }
    }

    /**
     * Save the current tree content as XML
     *
     * @throws IOException inherited from XMLBuilder
     */
    public void saveXML() throws IOException {
        // get all children of root because of the invisible root
        List<TreeItem<String>> elementList = new ArrayList<>();
        elementList.addAll(tree.getRoot().getChildren());

        // set the file destination
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML-Files", "*.xml"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        XMLBuilder.saveXML(elementList, fileChooser.showSaveDialog(new Stage()).getAbsolutePath(), treeItemGenerator);
    }
}
