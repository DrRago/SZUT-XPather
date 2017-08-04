package gui;

import TreeItemGenerator.TreeItemGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;

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

    private TreeItemGenerator treeItemGenerator;

    @FXML
    void initialize() throws Exception {
        treeItemGenerator = new TreeItemGenerator("test.xml");

        treeItemGenerator.setTableItems();

        tree.setRoot(treeItemGenerator.getRootNode());
    }

    public void clicked(MouseEvent mouseEvent){
        TreeItem<String> currentItem = (tree.getFocusModel().getFocusedItem());
        Element currentElement = treeItemGenerator.getElementList().get(tree.getRow(currentItem));

        // TODO: 04/08/2017 fix error on click on last element 
        breadcrump.setText(XPathHelper.getAbsolutePath(currentElement.getContent(0)).split("text\\(\\)")[0]);

        elementText.setText(currentElement.getText());

    }
}
