package by.fpmi.bsu.pianolane.controller;

import by.fpmi.bsu.pianolane.ui.pianoroll.Note;
import by.fpmi.bsu.pianolane.ui.NoteContainer;
import by.fpmi.bsu.pianolane.model.Channel;
import by.fpmi.bsu.pianolane.ui.GridPane;
import by.fpmi.bsu.pianolane.ui.pianoroll.Velocity;
import by.fpmi.bsu.pianolane.util.ChannelCollection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static by.fpmi.bsu.pianolane.util.CopyUtil.copy;
import static by.fpmi.bsu.pianolane.util.GlobalInstances.SEQUENCER;

@Component
@Slf4j
@Scope("prototype")
public class PianoRollController {

    @FXML
    public ScrollPane notesHorizontalScrollPane;
    @FXML
    public ScrollPane notesVerticalScrollPane;
    @FXML
    public ScrollPane velocityWrapperScrollPane;
    public AnchorPane velocityContainer;
    @FXML
    private ScrollPane velocityHorizontalScrollPane;
    @FXML
    private GridPane gridPane;
    @FXML
    private GridPane velocityPane;
    @FXML
    private Pane keyboardPane;
    @FXML
    private Button closeButton;
    @FXML
    private SplitPane splitPane;


    private final int NUM_KEYS = 60;        // 5 octaves (60 keys)
    private final double KEY_HEIGHT = 30;   // Высота клавиш
    private final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    // Параметры сетки
    private final double cellWidth = 50;   // Ширина клетки (по времени)
    private final double cellHeight = 30;  // Высота клетки (соответствует одной клавише)
    private final int numColumns = 48;     // Количество колонок (тактов)


    private Rectangle playheadLine;
    private Timeline playheadTimeline;

    private final int ticksPerColumn = 480; // Один столбец = 480 тиков (четверть ноты)

    private MainController mainController;

    private Integer channelId;
    private Channel channel;
    private ChannelCollection channelCollection;

    @Autowired
    public void setChannelCollection(ChannelCollection channelCollection) {
        this.channelCollection = channelCollection;
    }

    public PianoRollController(Integer channelId) {
        this.channelId = channelId;
    }


    @Autowired
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {
        splitPane.setDividerPositions(0.8);
        gridPane.setChannelId(channelId);
        drawKeyboard();
        drawGrid();
        initPlayhead();

        closeButton.setOnAction(event -> {
            mainController.closePianoRoll();
        });

        // Обработка клика по сетке для добавления ноты
        gridPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::handleGridClick);

        log.info("Fetching previously written notes for channel {}", channelId);
        List<Note> previouslyWrittenNotes = NoteContainer.getNotesForChannel(channelId);
        log.info("Fetched notes: {}", previouslyWrittenNotes);
        gridPane.getChildren().addAll(previouslyWrittenNotes);
        channel = channelCollection.getChannel(channelId);

        notesHorizontalScrollPane.setVvalue(0);
        notesHorizontalScrollPane.setVmax(0);
        velocityHorizontalScrollPane.setVvalue(0);
        velocityHorizontalScrollPane.setVmax(0);

        // Делегируем вертикальную прокрутку на внешний ScrollPane
        notesHorizontalScrollPane.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                // Передаем событие прокрутки внешнему ScrollPane
                double newValue = notesVerticalScrollPane.getVvalue() - event.getDeltaY() / notesVerticalScrollPane.getHeight();
                // Ограничиваем значение от 0 до 1
                newValue = Math.min(Math.max(newValue, 0), 1);
                notesVerticalScrollPane.setVvalue(newValue);
                event.consume(); // Предотвращаем дальнейшую обработку события
            }
        });

        // Bind horizontal scrolling between the two panes
        notesHorizontalScrollPane.hvalueProperty().bindBidirectional(
                velocityHorizontalScrollPane.hvalueProperty());

        // Instead of the existing scroll handlers, use this simpler approach:
        notesHorizontalScrollPane.setOnScroll(event -> {
            if (event.getDeltaY() != 0) {
                double newValue = notesVerticalScrollPane.getVvalue() - event.getDeltaY() / notesVerticalScrollPane.getHeight();
                notesVerticalScrollPane.setVvalue(Math.min(Math.max(newValue, 0), 1));
                event.consume();
            }
        });

        addRightStripe();
        System.out.println(velocityPane.getPrefHeight());
        addVelocityIndicators(); // Replace setupAdjustableIndicator() with this
    }

    private void addVelocityIndicators() {
        Velocity indicator = new Velocity(velocityPane, 220);
        velocityPane.getChildren().add(indicator);
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
        velocityPane.setPrefWidth(width );
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
            velocityPane.getChildren().add(copy(columnLine));

        }

        // Горизонтальные линии (клавиши) — оставляем как было
        for (int r = 0; r <= NUM_KEYS; r++) {
            double y = (NUM_KEYS - r) * cellHeight;
            Rectangle rowLine = new Rectangle(0, y, width, 1);
            rowLine.setFill(Color.GRAY);
            gridPane.getChildren().add(rowLine);
        }
    }

    private void addRightStripe() {
        // Create the vertical stripe
        Rectangle stripe = new Rectangle();
        stripe.setWidth(10);
        stripe.setFill(Color.web("#2b2b2b")); // Dark gray

        // Set the constraints in the AnchorPane
        AnchorPane.setRightAnchor(stripe, 0.0);
        AnchorPane.setTopAnchor(stripe, 20.0); // Starting below the separator band
        AnchorPane.setBottomAnchor(stripe, 0.0);

        // Adjust velocityHorizontalScrollPane to make room for stripe
        AnchorPane.setRightAnchor(velocityHorizontalScrollPane, 13.0);

        // Add stripe to the velocityContainer
        velocityContainer.getChildren().add(stripe);
    }


    // Обработка клика по сетке: рассчитываем позицию с учетом переворота оси Y
    private void handleGridClick(MouseEvent event) {
        if (event.isConsumed() || event.getButton() != MouseButton.PRIMARY || event.getEventType() != MouseEvent.MOUSE_CLICKED) {
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

        Integer id = channel.addNote(midiNote, startTick, noteDuration);

        // Отрисовка ноты в UI
        Note noteRect = new Note(id, channel, x, y, cellWidth, cellHeight);
        gridPane.getChildren().add(noteRect);
        NoteContainer.addNote(channelId, noteRect);
        log.info("Added Note to NoteContainer with key {}", channelId);
    }

    private void initPlayhead() {
        playheadLine = new Rectangle(0, 0, 2, cellHeight * NUM_KEYS);
        playheadLine.setFill(Color.RED);
    }

    protected void drawPlayhead() {
        gridPane.getChildren().add(playheadLine);
    }

    protected void removePlayhead() {
        gridPane.getChildren().remove(playheadLine);
    }

    protected void startPlayhead() {
        if (playheadTimeline != null) {
            playheadTimeline.stop();
        }
        drawPlayhead();

        playheadTimeline = new Timeline(new KeyFrame(Duration.millis(10), ev -> {
            if (SEQUENCER.isRunning()) {
                long tickPos = SEQUENCER.getTickPosition();
                double newX = (tickPos / (double) ticksPerColumn) * cellWidth;
                playheadLine.setX(newX);
            } else {
                playheadTimeline.stop();
                playheadLine.setX(0);
                removePlayhead();
            }
        }));
        playheadTimeline.setCycleCount(Timeline.INDEFINITE);
        playheadTimeline.play();
    }
}
