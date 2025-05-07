package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.wrappers.NoteEvent;
import by.fpmi.bsu.pianolane.model.DefaultChannel;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultChannelSerializer extends Serializer<DefaultChannel> {

    @Override
    public void write(Kryo kryo, Output output, DefaultChannel defaultChannel) {
        kryo.writeObject(output, defaultChannel.getNotesSequence());
        kryo.writeClassAndObject(output, defaultChannel.getNoteEvents());
        output.writeInt(defaultChannel.getChannelId());
        output.writeBoolean(defaultChannel.isMuted());
        output.writeBoolean(defaultChannel.isSoloed());
    }

    @Override
    @SuppressWarnings("unchecked")
    public DefaultChannel read(Kryo kryo, Input input, Class<? extends DefaultChannel> aClass) {
        AtomicInteger notesSequence = kryo.readObject(input, AtomicInteger.class);
        Map<Integer, NoteEvent> noteEvents = (Map<Integer, NoteEvent>) kryo.readClassAndObject(input);
        int channelId = input.readInt();
        boolean muted = input.readBoolean();
        boolean soloed = input.readBoolean();
        return DefaultChannel.builder()
                .channelId(channelId)
                .notesSequence(notesSequence)
                .noteEvents(noteEvents)
                .muted(muted)
                .soloed(soloed)
                .build();
    }
}
