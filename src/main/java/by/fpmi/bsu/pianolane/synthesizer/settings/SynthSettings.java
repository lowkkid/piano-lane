package by.fpmi.bsu.pianolane.synthesizer.settings;

import by.fpmi.bsu.pianolane.synthesizer.filter.FilterSettings;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SynthSettings {

    private final OscillatorSettings oscillatorASettings = new OscillatorSettings(true);
    private final OscillatorSettings oscillatorBSettings = new OscillatorSettings(false);
    private final FilterSettings filterSettings = new FilterSettings();

}