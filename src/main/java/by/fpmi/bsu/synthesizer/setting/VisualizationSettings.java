package by.fpmi.bsu.synthesizer.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisualizationSettings {

    private int numBands;

    private double smoothingFactor;

    private VisualizationSettings() {
        numBands = 1024;
        smoothingFactor = 0.5;
    }

    public static VisualizationSettings getInstance() {
        return VisualizationSettingsInstance.INSTANCE;
    }

    private static class VisualizationSettingsInstance {
        private static final VisualizationSettings INSTANCE = new VisualizationSettings();
    }
}
