package by.fpmi.bsu.pianolane.util;

import by.fpmi.bsu.pianolane.CustomReceiver;
import by.fpmi.bsu.pianolane.controller.PianoRollController;
import javax.sound.midi.ShortMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

@Slf4j
public class GlobalInstances {

    public static Sequencer SEQUENCER;
    public static Sequence SEQUENCE;
    public static Synthesizer SYNTHESIZER;
    public static ConfigurableApplicationContext SPRING_CONTEXT;
    public static PianoRollController CURRENT_PIANO_ROLL_CONTROLLER;
    public static Receiver DEFAULT_RECEIVER;

    static {
        try {
            SYNTHESIZER = MidiSystem.getSynthesizer();
            SYNTHESIZER.open();
            DEFAULT_RECEIVER = MidiSystem.getReceiver();
            SEQUENCER = MidiSystem.getSequencer(false);
            SEQUENCER.open();
            //SEQUENCER.getTransmitter().setReceiver(DEFAULT_RECEIVER);

            SEQUENCE = new Sequence(Sequence.PPQ, 480);
            SEQUENCER.setSequence(SEQUENCE);
            SEQUENCER.addMetaEventListener(meta -> {
                if (meta.getType() == 0x2F) {
                    ChannelCollection.stopSynthesizerChannels();
                }
            });
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

    public static void deleteTrack(Track track) {
        SEQUENCE.deleteTrack(track);
        try {
            SEQUENCER.setSequence(SEQUENCE);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
}
