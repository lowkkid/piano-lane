package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.config.SpringConfig;
import by.fpmi.bsu.pianolane.util.SpringFxmlLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class PianoLaneApplication extends Application {

    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        Parent root = springFxmlLoader.load("piano-roll.fxml");
        Scene scene = new Scene(root, 300, 300, Color.BLACK);


        primaryStage.setWidth(SCREEN_WIDTH);
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Full Screen Example");
        primaryStage.show();
    }

    @Override
    public void stop()  {
        springContext.close();
    }

    public static void main(String[] args) {
        launch();
    }
}