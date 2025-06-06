package by.fpmi.bsu.pianolane.synthesizer.settings;

import by.fpmi.bsu.pianolane.synthesizer.filter.FilterSettings;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@ToString
@Slf4j
public class SynthSettings {

    private boolean isEnabled = true;
    private final OscillatorSettings oscillatorASettings = new OscillatorSettings(true);
    private final OscillatorSettings oscillatorBSettings = new OscillatorSettings(false);
    private final FilterSettings filterSettings = new FilterSettings();

    public synchronized boolean isEnabled() {
        log.debug("Getting synth enabled state: {}", isEnabled);
        return isEnabled;
    }

    public synchronized void setEnabled(boolean enabled) {
        log.debug("Setting synth enabled state from {} to {}", isEnabled, enabled);
        this.isEnabled = enabled;
    }
}