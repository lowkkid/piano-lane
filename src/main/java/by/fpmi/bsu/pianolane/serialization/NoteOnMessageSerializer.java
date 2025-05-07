package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.wrappers.NoteOffMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteOnMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NoteOnMessageSerializer extends Serializer<NoteOnMessage> {

    @Override
    public void write(Kryo kryo, Output output, NoteOnMessage noteOnMessage) {
        output.writeInt(noteOnMessage.getChannel());
        output.writeInt(noteOnMessage.getMidiNote());
        output.writeInt(noteOnMessage.getVelocity());
    }

    @Override
    public NoteOnMessage read(Kryo kryo, Input input, Class<? extends NoteOnMessage> aClass) {
        int channel = input.readInt();
        int midiNote = input.readInt();
        int velocity = input.readInt();

        return new NoteOnMessage(channel, midiNote, velocity);
    }
}
