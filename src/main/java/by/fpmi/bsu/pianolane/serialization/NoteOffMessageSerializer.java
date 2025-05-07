package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.wrappers.NoteOffMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NoteOffMessageSerializer extends Serializer<NoteOffMessage> {
    @Override
    public void write(Kryo kryo, Output output, NoteOffMessage noteOffMessage) {
        output.writeInt(noteOffMessage.getChannel());
        output.writeInt(noteOffMessage.getMidiNote());
    }

    @Override
    public NoteOffMessage read(Kryo kryo, Input input, Class<? extends NoteOffMessage> aClass) {
        int channel = input.readInt();
        int midiNote = input.readInt();
        return new NoteOffMessage(channel, midiNote);
    }
}
