package by.fpmi.bsu.pianolane;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.DEFAULT_RECEIVER;

public class CustomReceiver implements Receiver {


    @Override
    public void send(MidiMessage message, long timeStamp) {
        DEFAULT_RECEIVER.send(message, timeStamp);
    }

    @Override
    public void close() {
        DEFAULT_RECEIVER.close();
    }
}
