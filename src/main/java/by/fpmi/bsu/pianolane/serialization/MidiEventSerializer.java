package by.fpmi.bsu.pianolane.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

public class MidiEventSerializer extends Serializer<MidiEvent> {

    @Override
    public void write(Kryo kryo, Output output, MidiEvent event) {
        kryo.writeObjectOrNull(output, event.getMessage(), MidiMessage.class);
        output.writeLong(event.getTick());
    }

    @Override
    public MidiEvent read(Kryo kryo, Input input, Class<? extends MidiEvent> type) {
        MidiMessage message = kryo.readObjectOrNull(input, MidiMessage.class);
        long tick = input.readLong();
        return new MidiEvent(message, tick);
    }
}