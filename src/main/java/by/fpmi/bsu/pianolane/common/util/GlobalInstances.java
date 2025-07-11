package by.fpmi.bsu.pianolane.common.util;

import by.fpmi.bsu.pianolane.midi.CustomReceiver;
import by.fpmi.bsu.pianolane.pianoroll.PianoRollController;
import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
public class GlobalInstances {

    public static Sequencer SEQUENCER;
    public static Sequence SEQUENCE;
    public static Synthesizer SYNTHESIZER;
    public static ConfigurableApplicationContext SPRING_CONTEXT;
    public static PianoRollController CURRENT_PIANO_ROLL_CONTROLLER;
    public static Receiver DEFAULT_RECEIVER;
    public static MidiChannel[] MIDI_CHANNELS;

    private static final ChannelCollection channelCollection = ChannelCollection.getInstance();

    static {
        try {
            SYNTHESIZER = MidiSystem.getSynthesizer();
            SYNTHESIZER.open();
            MIDI_CHANNELS = SYNTHESIZER.getChannels();
            DEFAULT_RECEIVER = SYNTHESIZER.getReceiver();
            SEQUENCER = MidiSystem.getSequencer(false);
            SEQUENCER.open();
            SEQUENCER.getTransmitter().setReceiver(new CustomReceiver());

            SEQUENCE = new Sequence(Sequence.PPQ, 480);
            SEQUENCER.setSequence(SEQUENCE);
            SEQUENCER.addMetaEventListener(meta -> {
                if (meta.getType() == 0x2F) {
                    channelCollection.stopSynthesizerChannels();
                }
            });
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateSequence(Sequence sequence) {
        SEQUENCE = sequence;
        try {
            SEQUENCER.setSequence(SEQUENCE);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }
}
