package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.config.SpringConfig;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SPRING_CONTEXT;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.PIANO_LANE_FXML;


public class PianoLaneApplication extends Application {

    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();


    @Override
    public void init() {
        SPRING_CONTEXT = new AnnotationConfigApplicationContext(SpringConfig.class);
    }


    @Override
    public void start(Stage primaryStage) {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        Parent root = springFxmlLoader.load(PIANO_LANE_FXML);
        Scene scene = new Scene(root, 300, 300, Color.BLACK);


        primaryStage.setWidth(SCREEN_WIDTH);
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Piano Lane");
        primaryStage.show();
    }

    @Override
    public void stop()  {
        SPRING_CONTEXT.close();
    }

    public static void main(String[] args) {
        launch();
    }
}