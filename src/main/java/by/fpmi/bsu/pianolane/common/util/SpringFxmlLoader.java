package by.fpmi.bsu.pianolane.common.util;

import by.fpmi.bsu.pianolane.common.util.constants.FxmlPaths;
import by.fpmi.bsu.pianolane.pianoroll.PianoRollController;
import by.fpmi.bsu.pianolane.synthesizer.filter.FilterController;
import by.fpmi.bsu.pianolane.synthesizer.oscillator.OscillatorController;
import by.fpmi.bsu.pianolane.synthesizer.controllers.SynthesizerController;
import by.fpmi.bsu.pianolane.synthesizer.filter.FilterSettings;
import by.fpmi.bsu.pianolane.synthesizer.settings.OscillatorSettings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public class SpringFxmlLoader {

    public Parent load(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
        loader.setControllerFactory(GlobalInstances.SPRING_CONTEXT::getBean);
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
                PianoRollController controller = GlobalInstances.SPRING_CONTEXT.getBean(PianoRollController.class, channelId);
                GlobalInstances.CURRENT_PIANO_ROLL_CONTROLLER = controller;
                return controller;
            }
            if (clazz == SynthesizerController.class) {
                return GlobalInstances.SPRING_CONTEXT.getBean(SynthesizerController.class, channelId);
            }
            return GlobalInstances.SPRING_CONTEXT.getBean(clazz);
        });
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Node loadOscillator(String oscName, boolean isSelected, OscillatorSettings settings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FxmlPaths.OSCILLATOR_FXML));
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

    public Node loadFilter(FilterSettings filterSettings) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(FxmlPaths.FILTERS_FXML));
            loader.setControllerFactory(param -> {
                if (param == FilterController.class) {
                    return new FilterController(filterSettings);
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
}
