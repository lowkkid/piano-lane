package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.NoteEvent;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.lang.reflect.Field;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

public class NoteEventSerializer extends Serializer<NoteEvent> {
    @Override
    public void write(Kryo kryo, Output output, NoteEvent noteEvent) {
        // Сериализация noteOnEvent и noteOffEvent
        kryo.writeObjectOrNull(output, noteEvent.getNoteOnEvent(), MidiEvent.class);
        kryo.writeObjectOrNull(output, noteEvent.getNoteOffEvent(), MidiEvent.class);

        // Сериализация сообщений
        kryo.writeObjectOrNull(output, noteEvent.getNoteOnMessage(), ShortMessage.class);
        kryo.writeObjectOrNull(output, noteEvent.getNoteOffMessage(), ShortMessage.class);
    }

    @Override
    public NoteEvent read(Kryo kryo, Input input, Class<? extends NoteEvent> type) {
        // Создаем пустой объект без вызова конструктора
        NoteEvent noteEvent = kryo.newInstance(type);

        // Десериализуем все поля
        MidiEvent noteOnEvent = kryo.readObjectOrNull(input, MidiEvent.class);
        MidiEvent noteOffEvent = kryo.readObjectOrNull(input, MidiEvent.class);
        ShortMessage noteOnMessage = kryo.readObjectOrNull(input, ShortMessage.class);
        ShortMessage noteOffMessage = kryo.readObjectOrNull(input, ShortMessage.class);

        // Устанавливаем поля с помощью рефлексии
        kryo.reference(noteEvent);
        setField(noteEvent, "noteOnEvent", noteOnEvent);
        setField(noteEvent, "noteOffEvent", noteOffEvent);
        setField(noteEvent, "noteOnMessage", noteOnMessage);
        setField(noteEvent, "noteOffMessage", noteOffMessage);

        return noteEvent;
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set field: " + fieldName, e);
        }
    }
}