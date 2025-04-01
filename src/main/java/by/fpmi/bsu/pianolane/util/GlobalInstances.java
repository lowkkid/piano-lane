package by.fpmi.bsu.pianolane.util;

import by.fpmi.bsu.pianolane.config.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

public class GlobalInstances {

    public static Sequencer SEQUENCER;
    public static Sequence SEQUENCE;
    public static Synthesizer SYNTHESIZER;
    public static ConfigurableApplicationContext SPRING_CONTEXT;

    static {
        try {
            SEQUENCER = MidiSystem.getSequencer();
            SEQUENCE = new Sequence(Sequence.PPQ, 480);
            SEQUENCER.open();
            SEQUENCER.setSequence(SEQUENCE);

            SYNTHESIZER = MidiSystem.getSynthesizer();
            SYNTHESIZER.open();
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static Track createTrack() {
        Track track = SEQUENCE.createTrack();
        try {
            SEQUENCER.open();
            SEQUENCER.setSequence(SEQUENCE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return track;
    }
}
