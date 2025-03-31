package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.MidiPlayer;
import by.fpmi.bsu.pianolane.Note;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

@Component
public class PianoRollController {

    @FXML
    private Pane gridPane;
    @FXML
    private Pane keyboardPane;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Spinner<Double> bpmSpinner;
    @FXML
    private Button closeButton;

    private final int NUM_KEYS = 60;        // 5 октав (60 клавиш)
    private final double KEY_HEIGHT = 30;   // Высота клавиш
    private final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    // Параметры сетки
    private final double cellWidth = 50;   // Ширина клетки (по времени)
    private final double cellHeight = 30;  // Высота клетки (соответствует одной клавише)
    private final int numColumns = 48;     // Количество колонок (тактов)

    // Список добавленных нот (прямоугольников)
    private final List<Rectangle> noteRectangles = new ArrayList<>();

    // Плейхед – вертикальная линия, показывающая текущий момент воспроизведения
    private Rectangle playheadLine;
    private Timeline playheadTimeline;

    private final int ticksPerColumn = 480; // Один столбец = 480 тиков (четверть ноты)

    private final MidiPlayer midiPlayer = MidiPlayer.getInstance();

    private MainController mainController;



    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        drawKeyboard();
        drawGrid();
        initPlayhead();
        initializeBpmSpinner();
        initializePlayAndStopButton();

//        closeButton.setOnAction(event -> {
//            mainController.closePianoRoll();
//        });

        // Обработка клика по сетке для добавления ноты
        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleGridClick);
    }

    private void initializeBpmSpinner() {
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 200.0, 120.0, 1.0);
        bpmSpinner.setValueFactory(valueFactory);

        valueFactory.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Double value) {
                if (value == null) return "";
                return String.format("%.1f", value);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.valueOf(string);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        });

        bpmSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            midiPlayer.setBpm(newVal.floatValue());
        });
    }

    private void initializePlayAndStopButton() {
        playButton.setOnAction(event -> {
            playNotes();
        });

        stopButton.setOnAction(event -> {
            stopNotes();
        });
    }

    // Инициализация плейхеда (вертикальная красная линия)
    private void initPlayhead() {
        // Размер плейхеда соответствует высоте всей сетки
        playheadLine = new Rectangle(0, 0, 2, cellHeight * NUM_KEYS);
        playheadLine.setFill(Color.RED);
        gridPane.getChildren().add(playheadLine);
    }

    // Отрисовка клавиатуры снизу вверх: нота с i=0 отрисовывается внизу
    private void drawKeyboard() {
        for (int i = 0; i < NUM_KEYS; i++) {
            int noteIndex = i % 12;
            int octaveNumber = (i / 12) + 1;
            boolean isBlackKey = NOTES[noteIndex].contains("#");

            double y = (NUM_KEYS - 1 - i) * KEY_HEIGHT;
            Rectangle key = new Rectangle(0, y, 120, KEY_HEIGHT);
            key.getStyleClass().add(isBlackKey ? "black-key" : "white-key");

            Text noteLabel = new Text(
                    5,
                    y + KEY_HEIGHT - 5,
                    NOTES[noteIndex] + octaveNumber);
            noteLabel.setFont(new Font(12));
            noteLabel.setFill(isBlackKey ? Color.WHITE : Color.BLACK);

            keyboardPane.getChildren().addAll(key, noteLabel);
        }
    }

    // Отрисовка сетки с перевернутой осью Y
    private void drawGrid() {
        double width = cellWidth * numColumns;
        double height = cellHeight * NUM_KEYS;
        gridPane.setPrefSize(width, height);

        // Допустим, у нас 4 четверти в такте (4/4).
        // Тогда каждые 4 вертикальные линии = 1 такт.
        // Условимся, что i — это индекс четверти (бита), а i % 4 == 0 — граница нового такта.

        for (int i = 0; i < numColumns; i++) {
            // Каждые 4 колонки считаем новой границей такта.
            boolean isMeasureLine = (i % 4 == 0);

            // Делаем более толстую линию, если это граница такта.
            double lineThickness = isMeasureLine ? 1.5 : 1;

            // Рисуем вертикальную линию от верха до низа сетки.
            Rectangle columnLine = new Rectangle(i * cellWidth, 0, lineThickness, height);
            columnLine.setFill(isMeasureLine ? Color.BLACK : Color.GRAY);
            gridPane.getChildren().add(columnLine);

        }

        // Горизонтальные линии (клавиши) — оставляем как было
        for (int r = 0; r <= NUM_KEYS; r++) {
            double y = (NUM_KEYS - r) * cellHeight;
            Rectangle rowLine = new Rectangle(0, y, width, 1);
            rowLine.setFill(Color.GRAY);
            gridPane.getChildren().add(rowLine);
        }
    }


    // Обработка клика по сетке: рассчитываем позицию с учетом переворота оси Y
    private void handleGridClick(MouseEvent event) {
        if (event.isConsumed() || event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        if (event.getTarget() instanceof Note) {
            return;
        }

        double x = event.getX();
        double y = event.getY();
        int col = (int) (x / cellWidth);
        int row = NUM_KEYS - 1 - (int) (y / cellHeight);
        double noteX = col * cellWidth;
        double noteY = (NUM_KEYS - 1 - row) * cellHeight;

        addNote(noteX, noteY, col, row);
    }


    private void addNote(double x, double y, int col, int row) {
        int midiNote = row + 24;
        int startTick = col * ticksPerColumn;
        int noteDuration = ticksPerColumn; // Фиксированная длительность (четверть ноты)

        Integer id = midiPlayer.addNote(midiNote, startTick, noteDuration);

        // Отрисовка ноты в UI
        Note noteRect = new Note(id, x, y, cellWidth, cellHeight);
        noteRect.setFill(Color.BLUE);
        noteRect.setStroke(Color.BLACK);
        gridPane.getChildren().add(noteRect);
        noteRectangles.add(noteRect);
    }

    private void stopNotes() {
        midiPlayer.stop();
    }

    private void playNotes() {
        midiPlayer.play();

        if (playheadTimeline != null) {
            playheadTimeline.stop();
        }

        playheadTimeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {
            if (SEQUENCER.isRunning()) {
                long tickPos = SEQUENCER.getTickPosition();
                double newX = (tickPos / (double) ticksPerColumn) * cellWidth;
                playheadLine.setX(newX);
            } else {
                playheadTimeline.stop();
                playheadLine.setX(0);
            }
        }));
        playheadTimeline.setCycleCount(Timeline.INDEFINITE);
        playheadTimeline.play();
    }
}
