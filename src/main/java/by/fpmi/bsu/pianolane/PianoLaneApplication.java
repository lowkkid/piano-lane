package by.fpmi.bsu.pianolane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class PianoLaneApplication extends Application {

    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("piano-lane.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 300, 300, Color.BLACK);


        primaryStage.setWidth(SCREEN_WIDTH);
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Full Screen Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}