package by.fpmi.bsu.synthesizer.settings;

import by.fpmi.bsu.synthesizer.newimpl.Waveform;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SynthSettings {

    private final OscillatorSettings oscillatorASettings = new OscillatorSettings(true);
    private final OscillatorSettings oscillatorBSettings = new OscillatorSettings(false);

}