package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteOffMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.midi.note.NoteOffMessage;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class NoteOffMessageSerializerTest extends AbstractSerializerTest {

    private static final Class<NoteOffMessage> clazz = NoteOffMessage.class;

    @Test
    void shouldSerialize() throws IOException {
        var original = createNoteOffMessage();

        serialize(original);

        var deserialize = deserialize(clazz);

        assertEquals(original, deserialize);
    }
}
