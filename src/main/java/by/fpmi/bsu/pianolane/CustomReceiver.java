package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.model.SynthesizerChannel;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
import by.fpmi.bsu.synthesizer.model.Synth;
import lombok.extern.slf4j.Slf4j;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.DEFAULT_RECEIVER;
import static by.fpmi.bsu.pianolane.util.LogUtil.getCommandName;

@Slf4j
public class CustomReceiver implements Receiver {

    Map<Integer, Deque<Synth>> midiVoices = new HashMap<>();


    @Override
    public void send(MidiMessage msg, long timeStamp) {
        if (!(msg instanceof ShortMessage sm)) {
            log.info("Custom receiver received a message that is not a ShortMessage");
            return;
        }

        int command = sm.getCommand();
        int channel = sm.getChannel();
        int note = sm.getData1();
        int velocity = sm.getData2();
        if (command != ShortMessage.PROGRAM_CHANGE && command != ShortMessage.CONTROL_CHANGE) {
            log.debug("Received command {} channel {}, data1 {}, data2 {}", getCommandName(command), channel, note, velocity);
        }

        if (ChannelCollection.isSynthesizer(channel)) {
            var synthesizer = ((SynthesizerChannel) ChannelCollection.getChannel(channel)).getSynthPlayer();
            if (command == ShortMessage.NOTE_ON && velocity > 0) {
                double freq = midiNoteToFreq(note);
                Synth synth = synthesizer.addVoice(freq, velocity / 127f);
                midiVoices.computeIfAbsent(note, k -> new ArrayDeque<>()).addLast(synth);
            } else if (command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                Deque<Synth> synths = midiVoices.get(note);
                if (synths != null && !synths.isEmpty()) {
                    Synth synthToRelease = synths.pollFirst();
                    if (synthToRelease != null) {
                        synthToRelease.noteOff();
                    }
                    if (synths.isEmpty()) {
                        midiVoices.remove(note);
                    }
                }
            }
        } else {
            DEFAULT_RECEIVER.send(msg, timeStamp);
        }
    }

    @Override
    public void close() {

    }

    private double midiNoteToFreq(int note) {
        return 440.0 * Math.pow(2, (note - 69) / 12.0);
    }
}
