package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteMidiEvent;
import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteOffMessage;
import static by.fpmi.bsu.pianolane.util.ObjectFactory.createNoteOnMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.wrappers.NoteMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteMidiEvent;
import java.io.IOException;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class NoteMidiEventSerializerTest extends AbstractSerializerTest {

    private static final Class<NoteMidiEvent> clazz = NoteMidiEvent.class;

    @ParameterizedTest
    @MethodSource("noteMessageProvider")
    void shouldSerializeAndDeserialize(NoteMessage noteMessage) throws IOException {
        var original = createNoteMidiEvent(noteMessage);

        serialize(original);
        var deserialized = deserialize(clazz);

        System.out.println(original);
        System.out.println(deserialized);
        assertEquals(original, deserialized);
    }

    static Stream<NoteMessage> noteMessageProvider() {
        return Stream.of(createNoteOffMessage(), createNoteOnMessage());
    }
}
