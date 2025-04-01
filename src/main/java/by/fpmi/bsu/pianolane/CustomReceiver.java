package by.fpmi.bsu.pianolane;

import by.fpmi.bsu.pianolane.util.ChannelCollection;
import by.fpmi.bsu.synthesizer.SoundGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static by.fpmi.bsu.pianolane.util.LogUtil.getCommandName;

@Slf4j
public class CustomReceiver implements Receiver {

    private final Receiver synthReceiver;
    private final Map<Integer, SoundGenerator> customGenerators = new HashMap<>();


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
        if (command != ShortMessage.PROGRAM_CHANGE) {
            log.info("Received command {} channel {}, data1 {}, data2 {}", getCommandName(command), channel, note, velocity);
        }

        if (ChannelCollection.isCustom(channel)) {
            if (command == ShortMessage.NOTE_ON && velocity > 0) {
                double freq = midiNoteToFreq(note);
                SoundGenerator generator = new SoundGenerator(freq);
                try {
                    generator.playSound();
                    customGenerators.put(note + (channel * 1000), generator);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (command == ShortMessage.NOTE_OFF || (command == ShortMessage.NOTE_ON && velocity == 0)) {
                SoundGenerator gen = customGenerators.remove(note + (channel * 1000));
                if (gen != null) gen.stopSound();
            } else {
                synthReceiver.send(msg, timeStamp);
            }
        } else {
            synthReceiver.send(msg, timeStamp);
        }
    }

    @Override
    public void close() {
        for (var generator : customGenerators.values()) {
            generator.stopSound();
        }
    }

    private double midiNoteToFreq(int note) {
        return 440.0 * Math.pow(2, (note - 69) / 12.0);
    }
}
