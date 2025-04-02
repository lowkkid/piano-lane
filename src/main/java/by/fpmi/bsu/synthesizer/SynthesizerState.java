package by.fpmi.bsu.synthesizer;


import by.fpmi.bsu.synthesizer.models.WaveformType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
public class SynthesizerState {

    private double frequency = 440;
    private double amplitude = 50;

    private boolean lowPassEnabled = false;
    private double lowPassCutoff = 1000;

    private boolean highPassEnabled = false;
    private double highPassCutoff = 500;

    private WaveformType waveformType;
}
