package fxmlexercise.completed;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.cirdles.commons.util.ResourceExtractor;

public class CompletedMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("FXMLExercise");

        Parent root = FXMLLoader.load(
                new ResourceExtractor(CompletedMain.class).extractResourceAsPath("completed-frame.fxml").toUri().toURL()
        );

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
