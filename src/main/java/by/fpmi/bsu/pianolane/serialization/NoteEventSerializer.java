package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.wrappers.NoteMidiEvent;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NoteEventSerializer extends Serializer<NoteEvent> {
    @Override
    public void write(Kryo kryo, Output output, NoteEvent noteEvent) {
        kryo.writeObjectOrNull(output, noteEvent.getNoteOnEvent(), NoteMidiEvent.class);
        kryo.writeObjectOrNull(output, noteEvent.getNoteOffEvent(), NoteMidiEvent.class);
    }

    @Override
    public NoteEvent read(Kryo kryo, Input input, Class<? extends NoteEvent> type) {
        NoteMidiEvent noteOnEvent = kryo.readObjectOrNull(input, NoteMidiEvent.class);
        NoteMidiEvent noteOffEvent = kryo.readObjectOrNull(input, NoteMidiEvent.class);

        return NoteEvent.builder()
                .noteOffEvent(noteOffEvent)
                .noteOnEvent(noteOnEvent)
                .build();
    }
}