package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteOnMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.midi.note.NoteOnMessage;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class NoteOnMessageSerializerTest extends AbstractSerializerTest {

    private static final Class<NoteOnMessage> clazz = NoteOnMessage.class;

    @Test
    void shouldSerialize() throws IOException {
        var original = createNoteOnMessage();

        serialize(original);

        var deserialize = deserialize(clazz);

        assertEquals(original, deserialize);
    }
}
