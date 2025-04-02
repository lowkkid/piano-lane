package by.fpmi.bsu.pianolane;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.sound.midi.InvalidMidiDataException;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCE;
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
        try {
            SEQUENCER.setSequence(SEQUENCE);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
        SEQUENCER.setTickPosition(0);
        SEQUENCER.setTempoInBPM(bpm);
        SEQUENCER.start();
    }

    public void stop() {
        SEQUENCER.stop();
        SEQUENCER.setTickPosition(0);
    }
}
