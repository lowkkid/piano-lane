package by.fpmi.bsu.pianolane.util;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SYNTHESIZER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.sound.midi.Instrument;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstrumentsUtil {

    private static final Map<Integer, Instrument> INSTRUMENTS = new HashMap<>();

    static {
        var instruments = SYNTHESIZER.getAvailableInstruments();
        IntStream.range(0, instruments.length).forEach(i -> INSTRUMENTS.put(i, instruments[i]));
    }

    public static List<Instrument> getInstruments() {
        return new ArrayList<>(INSTRUMENTS.values());
    }

    public static Instrument getInstrumentById(int instrumentId) {
        return INSTRUMENTS.get(instrumentId);
    }
}
