package by.fpmi.bsu.pianolane.util;

import by.fpmi.bsu.pianolane.controller.PianoRollController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SPRING_CONTEXT;

public class SpringFxmlLoader {

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(SPRING_CONTEXT::getBean);
        return loader.load();
    }

    public Parent load(String fxmlPath, Integer channelId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(clazz -> {
            if (clazz == PianoRollController.class) {
                return SPRING_CONTEXT.getBean(PianoRollController.class, channelId);
            }
            return SPRING_CONTEXT.getBean(clazz);
        });
        return loader.load();
    }
}
