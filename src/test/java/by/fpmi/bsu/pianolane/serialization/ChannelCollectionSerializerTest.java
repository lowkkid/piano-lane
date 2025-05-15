package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createChannelCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ChannelCollectionSerializerTest extends AbstractSerializerTest {

    private static final Class<ChannelCollection> clazz = ChannelCollection.class;

    @Test
    void shouldSerializeAndDeserialize() throws IOException {
        var original = createChannelCollection();

        serialize(original);

        var deserialized = deserialize(clazz);

        assertEquals(original, deserialized);
    }
}
