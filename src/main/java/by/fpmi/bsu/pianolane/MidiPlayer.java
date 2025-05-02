package by.fpmi.bsu.pianolane;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import static by.fpmi.bsu.pianolane.model.ChannelCollection.startSynthesizerChannels;
import static by.fpmi.bsu.pianolane.model.ChannelCollection.stopSynthesizerChannels;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

@Component
@Getter
@Setter
public class MidiPlayer {

    private float bpm = 120.0f;

    public void play() {
        if (SEQUENCER.isRunning()) {
            SEQUENCER.stop();
        }
        startSynthesizerChannels();
        SEQUENCER.setTickPosition(0);
        SEQUENCER.setTempoInBPM(bpm);
        SEQUENCER.start();
    }

    public void stop() {
        stopSynthesizerChannels();
        SEQUENCER.stop();
        SEQUENCER.setTickPosition(0);
    }
}
