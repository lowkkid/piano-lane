package by.fpmi.bsu.pianolane.midi.note.serialization;

import by.fpmi.bsu.pianolane.midi.note.NoteMessage;
import by.fpmi.bsu.pianolane.midi.note.NoteMidiEvent;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NoteMidiEventSerializer extends Serializer<NoteMidiEvent> {


    @Override
    public void write(Kryo kryo, Output output, NoteMidiEvent noteMidiEvent) {
        kryo.writeClassAndObject(output, noteMidiEvent.noteMessage());
        output.writeLong(noteMidiEvent.getTick());
    }

    @Override
    public NoteMidiEvent read(Kryo kryo, Input input, Class<? extends NoteMidiEvent> clazz) {
        NoteMessage noteMessage = (NoteMessage) kryo.readClassAndObject(input);
        long tick = input.readLong();
        return new NoteMidiEvent(noteMessage, tick);
    }
}
