package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.model.ChannelCollection;
import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.model.DefaultChannel;
import by.fpmi.bsu.pianolane.wrappers.NoteMidiEvent;
import by.fpmi.bsu.pianolane.wrappers.NoteOffMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteOnMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

abstract class AbstractSerializerTest {

    @TempDir
    protected Path tempDir;
    protected Kryo kryo;
    protected File testFile;

    @BeforeEach
    void setUp() {
        kryo = new Kryo();
        kryo.register(NoteEvent.class, new NoteEventSerializer());
        kryo.register(DefaultChannel.class, new DefaultChannelSerializer());
        kryo.register(AtomicInteger.class, new DefaultSerializers.AtomicIntegerSerializer());
        kryo.register(HashMap.class);
        kryo.register(NoteOffMessage.class, new NoteOffMessageSerializer());
        kryo.register(NoteOnMessage.class, new NoteOnMessageSerializer());
        kryo.register(NoteMidiEvent.class, new NoteMidiEventSerializer());
        kryo.register(ChannelCollection.class, new ChannelCollectionSerializer());
        testFile = tempDir.resolve("test-message.dat").toFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFile.toPath());
    }

    protected <T> void serialize(T t) throws IOException {
        try (Output output = new Output(new FileOutputStream(testFile))) {
            kryo.writeObject(output, t);
        }
    }

    protected <T> T deserialize(Class<T> targetClass) throws IOException {
        try (Input input = new Input(new FileInputStream(testFile))) {
            return kryo.readObject(input, targetClass);
        }
    }
}
