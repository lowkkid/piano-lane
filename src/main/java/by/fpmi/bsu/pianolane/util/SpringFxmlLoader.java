package by.fpmi.bsu.pianolane.util;

import by.fpmi.bsu.pianolane.controller.PianoRollController;
import by.fpmi.bsu.synthesizer.controllers.OscillatorController;
import by.fpmi.bsu.synthesizer.controllers.SynthesizerController;
import by.fpmi.bsu.synthesizer.settings.OscillatorSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.CURRENT_PIANO_ROLL_CONTROLLER;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SPRING_CONTEXT;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.FILTERS_FXML;
import static by.fpmi.bsu.pianolane.util.constants.FxmlPaths.OSCILLATOR_FXML;

public class SpringFxmlLoader {

    public Parent load(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(SPRING_CONTEXT::getBean);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent load(String fxmlPath, Integer channelId) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(clazz -> {
            if (clazz == PianoRollController.class) {
                PianoRollController controller = SPRING_CONTEXT.getBean(PianoRollController.class, channelId);
                CURRENT_PIANO_ROLL_CONTROLLER = controller;
                return controller;
            }
            if (clazz == SynthesizerController.class) {
                return SPRING_CONTEXT.getBean(SynthesizerController.class, channelId);
            }
            return SPRING_CONTEXT.getBean(clazz);
        });
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Node loadOscillator(String oscName, boolean isSelected, OscillatorSettings settings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(OSCILLATOR_FXML));
            loader.setControllerFactory(param -> {
                if (param == OscillatorController.class) {
                    return new OscillatorController(oscName, isSelected, settings);
                } else {
                    try {
                        return param.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Node loadFilter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FILTERS_FXML));
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
