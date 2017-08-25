import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Run extends Application{
    public static void main(String args[]) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("gui.fxml"));

        // set stage parameters
        primaryStage.setTitle("XPather");

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}