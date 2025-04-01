package by.fpmi.bsu.synthesizer.setting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterSettings {

    private boolean lowPassEnabled = false;

    private double lowPassCutoff;

    private boolean highPassEnabled = false;

    private double highPassCutoff;

    public static FilterSettings getInstance() {
        return FilterSettingsInstance.INSTANCE;
    }

    private FilterSettings() {
        lowPassCutoff = 1000;
        highPassCutoff = 500;
    }

    private static class FilterSettingsInstance {
        private static final FilterSettings INSTANCE = new FilterSettings();
    }
}
