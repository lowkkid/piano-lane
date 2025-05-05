package by.fpmi.bsu.pianolane.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class ShortMessageSerializer extends Serializer<ShortMessage> {

    @Override
    public void write(Kryo kryo, Output output, ShortMessage message) {
        output.writeInt(message.getCommand());
        output.writeInt(message.getChannel());
        output.writeInt(message.getData1());
        output.writeInt(message.getData2());
    }

    @Override
    public ShortMessage read(Kryo kryo, Input input, Class<? extends ShortMessage> type) {
        int command = input.readInt();
        int channel = input.readInt();
        int data1 = input.readInt();
        int data2 = input.readInt();

        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, data1, data2);
            return message;
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Error creating ShortMessage", e);
        }
    }
}