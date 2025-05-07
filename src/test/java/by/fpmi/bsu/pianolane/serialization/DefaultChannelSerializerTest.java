package by.fpmi.bsu.pianolane.serialization;

import static by.fpmi.bsu.pianolane.util.ObjectFactory.createDefaultChannel;
import static org.junit.jupiter.api.Assertions.assertEquals;

import by.fpmi.bsu.pianolane.model.DefaultChannel;
import org.junit.jupiter.api.Test;

class DefaultChannelSerializerTest extends AbstractSerializerTest {

    private static final Class<DefaultChannel> clazz = DefaultChannel.class;

    @Test
    void shouldSerializeAndDeserializeDefaultChannel() throws Exception {
        var original = createDefaultChannel();

        serialize(original);

        var deserialized = deserialize(clazz);

        assertEquals(original, deserialized);
    }
}
