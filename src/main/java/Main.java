import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {
    private Duke duke = new Duke();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Bob the Chatbot");
            Image icon = new Image(this.getClass().getResourceAsStream("/images/icon.jpg"));
            stage.getIcons().add(icon);
            fxmlLoader.<MainWindow>getController().setDuke(duke);
            duke.getUi().stage = stage;
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
