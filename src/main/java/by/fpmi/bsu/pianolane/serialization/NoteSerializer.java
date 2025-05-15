package by.fpmi.bsu.pianolane.serialization;

import by.fpmi.bsu.pianolane.ui.pianoroll.Note;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NoteSerializer extends Serializer<Note> {

    @Override
    public void write(Kryo kryo, Output output, Note note) {
        output.writeInt(note.getNoteId());
        output.writeDouble(note.getX());
        output.writeDouble(note.getY());
        output.writeDouble(note.getWidth());
        output.writeDouble(note.getHeight());
    }

    @Override
    public Note read(Kryo kryo, Input input, Class<? extends Note> clazz) {
        int noteId = input.readInt();
        double x = input.readDouble();
        double y = input.readDouble();
        double width = input.readDouble();
        double height = input.readDouble();
        return new Note(noteId, x, y, width, height);
    }
}
