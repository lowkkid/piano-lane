package by.fpmi.bsu.pianolane.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.CONTEXT;

public class SpringFxmlLoader {

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(CONTEXT::getBean);
        return loader.load();
    }
}
