package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import org.junit.jupiter.api.Test;

class NoteEventSerializerTest extends AbstractSerializerTest {

    private static final Class<NoteEvent> noteEventClass = NoteEvent.class;

    @Test
    void shouldSerializeAndDeserializeNoteEvent() throws Exception {
        NoteEvent original = createNoteEvent();

        serialize(original);

        NoteEvent deserialized = deserialize(noteEventClass);

        assertEquals(original, deserialized);
    }
}
