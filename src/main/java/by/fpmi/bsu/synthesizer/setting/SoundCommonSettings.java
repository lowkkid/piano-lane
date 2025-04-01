package by.fpmi.bsu.synthesizer.setting;

import by.fpmi.bsu.synthesizer.models.WaveformType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoundCommonSettings {

    private WaveformType waveformType;

    private double amplitude;

    private SoundCommonSettings() {
        waveformType = WaveformType.SINE;
        amplitude = 50;
    }

    public static SoundCommonSettings getInstance() {
        return SoundCommonSettingsInstance.INSTANCE;
    }

    private static class SoundCommonSettingsInstance {
        private static final SoundCommonSettings INSTANCE = new SoundCommonSettings();
    }
}
