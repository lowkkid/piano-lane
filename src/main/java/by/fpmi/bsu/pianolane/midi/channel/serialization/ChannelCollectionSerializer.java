package by.fpmi.bsu.pianolane.midi.channel.serialization;

import by.fpmi.bsu.pianolane.midi.channel.model.Channel;
import by.fpmi.bsu.pianolane.midi.channel.ChannelCollection;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.util.concurrent.Executors;

public class ChannelCollectionSerializer extends Serializer<ChannelCollection> {
    @Override
    public void write(Kryo kryo, Output output, ChannelCollection channelCollection) {
        output.writeInt(channelCollection.getChannels().length);
        for (var channel : channelCollection.getChannels()) {
            kryo.writeClassAndObject(output, channel);
        }
        output.writeInt(channelCollection.getSynthesizersThreadPoolCount());
    }

    @Override
    public ChannelCollection read(Kryo kryo, Input input, Class<? extends ChannelCollection> clazz) {
        int length = input.readInt();
        Channel[] channels = new Channel[length];
        for (int i = 0; i < length; i++) {
            channels[i] = (Channel) kryo.readClassAndObject(input);
        }
        int synthesizersThreadPoolCount = input.readInt();
        var executorService = synthesizersThreadPoolCount > 0
                ? Executors.newFixedThreadPool(synthesizersThreadPoolCount)
                : null;
        return ChannelCollection.builder()
                .channels(channels)
                .synthesizersThreadPoolCount(synthesizersThreadPoolCount)
                .executorService(executorService)
                .build();
    }
}
