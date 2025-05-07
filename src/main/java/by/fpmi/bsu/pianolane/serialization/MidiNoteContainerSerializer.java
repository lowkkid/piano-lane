package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNote;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNoteContainer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidiNoteContainerSerializer extends Serializer<MidiNoteContainer> {

    @Override
    public void write(Kryo kryo, Output output, MidiNoteContainer object) {
        output.writeInt(object.getNotesByChannel().size());
        for (Map.Entry<Integer, List<MidiNote>> entry : object.getNotesByChannel().entrySet()) {
            output.writeInt(entry.getKey());
            kryo.writeClassAndObject(output, entry.getValue());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public MidiNoteContainer read(Kryo kryo, Input input, Class<? extends MidiNoteContainer> type) {
        MidiNoteContainer instance = MidiNoteContainer.getInstance();
        Map<Integer, List<MidiNote>> newMap = new HashMap<>();
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            int channelId = input.readInt();
            List<MidiNote> notes = (List<MidiNote>) kryo.readClassAndObject(input);
            newMap.put(channelId, notes);
        }
        instance.getNotesByChannel().clear();
        instance.getNotesByChannel().putAll(newMap);
        return instance;
    }
}