package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.ui.pianoroll.Velocity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class VelocitySerializer extends Serializer<Velocity> {

    @Override
    public void write(Kryo kryo, Output output, Velocity velocity) {
        output.writeInt(velocity.getNoteId());
        output.writeDouble(velocity.getHeightPercentage());
        output.writeDouble(velocity.getX());
    }

    @Override
    public Velocity read(Kryo kryo, Input input, Class<? extends Velocity> aClass) {
        int noteId = input.readInt();
        double heightPercentage = input.readDouble();
        double x = input.readDouble();
        return new Velocity(noteId, null, x, heightPercentage);
    }
}
