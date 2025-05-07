package by.fpmi.bsu.pianolane.project;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCE;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.updateSequence;
import static by.fpmi.bsu.pianolane.util.TracksUtil.getTrackId;
import static com.esotericsoftware.kryo.serializers.DefaultSerializers.AtomicIntegerSerializer;

import by.fpmi.bsu.pianolane.controller.ChannelRackController;
import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
import by.fpmi.bsu.pianolane.model.DefaultChannel;
import by.fpmi.bsu.pianolane.serialization.ChannelCollectionSerializer;
import by.fpmi.bsu.pianolane.serialization.DefaultChannelSerializer;
import by.fpmi.bsu.pianolane.serialization.MidiNoteContainerSerializer;
import by.fpmi.bsu.pianolane.serialization.NoteEventSerializer;
import by.fpmi.bsu.pianolane.serialization.NoteMidiEventSerializer;
import by.fpmi.bsu.pianolane.serialization.NoteOffMessageSerializer;
import by.fpmi.bsu.pianolane.serialization.NoteOnMessageSerializer;
import by.fpmi.bsu.pianolane.serialization.NoteSerializer;
import by.fpmi.bsu.pianolane.serialization.VelocitySerializer;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNote;
import by.fpmi.bsu.pianolane.ui.pianoroll.MidiNoteContainer;
import by.fpmi.bsu.pianolane.ui.pianoroll.Note;
import by.fpmi.bsu.pianolane.ui.pianoroll.Velocity;
import by.fpmi.bsu.pianolane.util.LogUtil;
import by.fpmi.bsu.pianolane.util.TracksUtil;
import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.wrappers.NoteMidiEvent;
import by.fpmi.bsu.pianolane.wrappers.NoteOffMessage;
import by.fpmi.bsu.pianolane.wrappers.NoteOnMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import org.springframework.stereotype.Component;

@Component
public class ProjectManager {

    private final String extension = ".plp";
    private final int formatVersion = 1;
    private final byte[] signature = "!PLP".getBytes(StandardCharsets.UTF_8);
    private final Kryo kryo = new Kryo();

    private final ChannelRackController channelRackController;

    public ProjectManager(ChannelRackController channelRackController) {
        this.channelRackController = channelRackController;
    }

    @PostConstruct
    public void init() {
        kryo.register(HashMap.class);
        kryo.register(ArrayList.class);
        kryo.register(AtomicInteger.class, new AtomicIntegerSerializer());
        kryo.register(NoteEvent.class, new NoteEventSerializer());
        kryo.register(DefaultChannel.class, new DefaultChannelSerializer());
        kryo.register(NoteOffMessage.class, new NoteOffMessageSerializer());
        kryo.register(NoteOnMessage.class, new NoteOnMessageSerializer());
        kryo.register(NoteMidiEvent.class, new NoteMidiEventSerializer());
        kryo.register(ChannelCollection.class, new ChannelCollectionSerializer());
        kryo.register(Note.class, new NoteSerializer());
        kryo.register(Velocity.class, new VelocitySerializer());
        kryo.register(MidiNote.class, new FieldSerializer<>(kryo, MidiNote.class));
        kryo.register(MidiNoteContainer.class, new MidiNoteContainerSerializer());
    }

    public void saveProject(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // Запись MIDI данных во временный поток
            ByteArrayOutputStream midiOut = new ByteArrayOutputStream();
            int[] fileTypes = MidiSystem.getMidiFileTypes(SEQUENCE);
            if (fileTypes.length > 0) {
                int fileType = contains(fileTypes, 1) ? 1 : fileTypes[0];
                MidiSystem.write(SEQUENCE, fileType, midiOut);
            }
            byte[] midiData = midiOut.toByteArray();

            // Сигнатура и версия формата
            fos.write(signature);
            fos.write(formatVersion); // Версия формата

            // Запись размера MIDI данных и количества Kryo объектов
            ByteBuffer midiDataSizeHeader = ByteBuffer.allocate(4);
            midiDataSizeHeader.putInt(midiData.length);
            fos.write(midiDataSizeHeader.array());

            // Запись MIDI данных
            fos.write(midiData);

            Output output = new Output(fos);
            kryo.writeObject(output, ChannelCollection.getInstance());
            kryo.writeObject(output, MidiNoteContainer.getInstance());
            output.close();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла с MIDI и Kryo данными", e);
        }
    }

    public void loadFromFile(String absolutePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(absolutePath)) {
            // Проверка сигнатуры
            byte[] readSignature = new byte[4];
            fis.read(readSignature);
            if (!Arrays.equals(readSignature, signature)) {
                throw new IOException("Wrong file format");
            }

            // Чтение версии
            int readVersion = fis.read();
            if (readVersion != formatVersion) {
                throw new IOException("Unsupported format version: " + readVersion);
            }

            // Чтение заголовка
            byte[] headerData = new byte[4];
            fis.read(headerData);
            int midiSize = ByteBuffer.wrap(headerData).getInt();

            // Чтение MIDI данных
            byte[] midiData = new byte[midiSize];
            fis.read(midiData);

            // Обработка MIDI
            ByteArrayInputStream midiStream = new ByteArrayInputStream(midiData);
            Sequence sequence = MidiSystem.getSequence(midiStream);
            Arrays.stream(sequence.getTracks()).forEach(LogUtil::logAllTrackEvents);
            channelRackController.loadFromTracks(sequence.getTracks());
            updateSequence(sequence);

            Input input = new Input(fis);
            ChannelCollection readChannelCollection = kryo.readObject(input, ChannelCollection.class);
            Arrays.stream(sequence.getTracks()).forEach(track -> {
                int channelId = getTrackId(track);
                Channel target = readChannelCollection.getChannel(channelId);
                if (target != null) {
                    target.setTrack(track);
                }
            });

            ChannelCollection.getInstance().resetFrom(readChannelCollection);

            kryo.readObject(input, MidiNoteContainer.class);
            input.close();
        }
    }

    private boolean contains(int[] array, int value) {
        for (int item : array) {
            if (item == value) return true;
        }
        return false;
    }
}
