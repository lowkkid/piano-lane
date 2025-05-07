package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.model.ChannelCollection;
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
    public ChannelCollection read(Kryo kryo, Input input, Class<? extends ChannelCollection> aClass) {
        int length = input.readInt();
        System.out.println(length);
        Channel[] channels = new Channel[length];
        for (int i = 0; i < length; i++) {
            channels[i] = (Channel) kryo.readClassAndObject(input);
            System.out.println(channels[i]);
        }
        int synthesizersThreadPoolCount = input.readInt();
        System.out.println(synthesizersThreadPoolCount);
        var executorService = synthesizersThreadPoolCount > 0 ? Executors.newFixedThreadPool(synthesizersThreadPoolCount) : null;
        return ChannelCollection.builder()
                .channels(channels)
                .synthesizersThreadPoolCount(synthesizersThreadPoolCount)
                .executorService(executorService)
                .build();
    }
}
