package gui;

import XMLParser.Parser;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

/**
 * Created by Leonhard.Gahr on 31/05/2017
 */

public class Controller {
    @FXML
    private TreeView<String> tree;

    @FXML
    void initialize() throws Exception {
        Parser parser = new Parser("test.xml");

        parser.setTableItems();

        tree.setRoot(parser.getRootNode());
    }
}
