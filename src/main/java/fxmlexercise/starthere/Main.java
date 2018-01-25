package fxmlexercise.starthere;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("FXMLExample");

        Parent root = FXMLLoader.load(getClass().getResource("selector.fxml"));

        primaryStage.setScene(new Scene(root, 350, 200));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
