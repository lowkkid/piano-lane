package by.fpmi.bsu.synthesizer.controllers;


import static by.fpmi.bsu.synthesizer.newimpl.SoundUtil.generateWaveform;

import by.fpmi.bsu.synthesizer.newimpl.Waveform;
import by.fpmi.bsu.synthesizer.settings.OscillatorSettings;
import by.fpmi.bsu.synthesizer.ui.KnobControl;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OscillatorController {

    private static final Color ATTACK_KNOB_COLOR = Color.rgb(255, 100, 100);
    private static final Color DECAY_KNOB_COLOR = Color.rgb(100, 255, 255);
    private static final Color SUSTAIN_KNOB_COLOR = Color.rgb(255, 255, 100);
    private static final Color RELEASE_KNOB_COLOR = Color.rgb(255, 100, 255);
    private static final int ADSR_KNOB_SIZE = 20;
    private static final int MAIN_KNOB_SIZE = 14;

    private static final double ATTACK_TIME_MAX_VALUE = 3;
    private static final double DECAY_TIME_MAX_VALUE = 3;
    private static final double RELEASE_TIME_MAX_VALUE = 3;

    @FXML
    public Canvas waveformCanvas;

    @FXML
    public Canvas envelopeCanvas;
    public CheckBox switchCheckbox;
    private String oscillatorNameParam;
    private boolean lockedParam;
    public Label oscillatorNameLabel;

    private OscillatorSettings settings;

    public OscillatorController(String oscillatorNameParam, boolean isLocked, OscillatorSettings settings) {
        this.oscillatorNameParam = oscillatorNameParam;
        this.lockedParam = isLocked;
        this.settings = settings;
    }

    @FXML
    private ChoiceBox<String> waveformChoiceBox;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private HBox envelopeControlBox;

    @FXML
    private HBox mainControlBox;

//    private double attackTime = 0.0;  // 0.0 to 0.4
//    private double decayTime = 0.0;   // 0.0 to 0.4
//    private double sustainLevel = 1; // 0.0 to 1.0
//    private double releaseTime = 0.0;  // 0.0 to 0.4
//
//    private int unisonVoices = 1;      // 1 to 16
//    private double detuneAmount = 0.0; // 0.0 to 1.0
//    private double blendAmount = 0.5;  // 0.0 to 1.0
//    private double phaseAmount = 0.0;  // 0.0 to 1.0
//    private Waveform waveform;

    @FXML
    private VBox oscillatorRoot;

    private OscillatorController oscillatorController;


    public void initialize() {
        switchCheckbox.setOnAction(event -> {
            boolean locked = !switchCheckbox.isSelected();
            switchPanel(locked);
            settings.setEnabled(switchCheckbox.isSelected());
        });

        switchCheckbox.setSelected(!lockedParam);
        switchPanel(lockedParam);

        oscillatorController = this;
        // Initialize waveform canvas
        GraphicsContext gcWave = waveformCanvas.getGraphicsContext2D();
        gcWave.setFill(Color.BLACK);
        gcWave.fillRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());

        GraphicsContext gcADSR = envelopeCanvas.getGraphicsContext2D();
        gcADSR.setFill(Color.BLACK);
        gcADSR.fillRect(0, 0, envelopeCanvas.getWidth(), envelopeCanvas.getHeight());

        // Initialize oscillator presets
        waveformChoiceBox.getItems().addAll(Waveform.SINE.getDisplayName(),
                Waveform.SQUARE.getDisplayName(),
                Waveform.TRIANGLE.getDisplayName(), Waveform.SAW.getDisplayName());

        waveformChoiceBox.setOnAction(event -> {
            String selectedWaveform = waveformChoiceBox.getValue();
            settings.setWaveform(Waveform.fromDisplayName(selectedWaveform));
            redrawWaveform();
        });
        waveformChoiceBox.setValue(settings.getWaveform().getDisplayName());


        prevButton.setOnAction(event -> {
            int currentIndex = waveformChoiceBox.getItems().indexOf(waveformChoiceBox.getValue());
            int newIndex = (currentIndex - 1 + waveformChoiceBox.getItems().size()) % waveformChoiceBox.getItems().size();
            waveformChoiceBox.setValue(waveformChoiceBox.getItems().get(newIndex));
        });

        nextButton.setOnAction(event -> {
            int currentIndex = waveformChoiceBox.getItems().indexOf(waveformChoiceBox.getValue());
            int newIndex = (currentIndex + 1) % waveformChoiceBox.getItems().size();
            waveformChoiceBox.setValue(waveformChoiceBox.getItems().get(newIndex));
        });

        setupMainControls();
        setupEnvelopeControls();
        drawEnvelope();
        oscillatorNameLabel.setText(oscillatorNameParam);

    }

    private void switchPanel(boolean locked) {
        waveformChoiceBox.setDisable(locked);
        prevButton.setDisable(locked);
        nextButton.setDisable(locked);
        mainControlBox.setDisable(locked);
        envelopeControlBox.setDisable(locked);

        if (locked) {
            if (!oscillatorRoot.getStyleClass().contains("disabled-panel")) {
                oscillatorRoot.getStyleClass().add("disabled-panel");
            }
        } else {
            oscillatorRoot.getStyleClass().remove("disabled-panel");
        }
    }

    private void setupMainControls() {
        // Clear any existing controls
        mainControlBox.getChildren().clear();

        // Set spacing and alignment
        mainControlBox.setSpacing(15);
        mainControlBox.setAlignment(Pos.CENTER);
        mainControlBox.setPadding(new Insets(5, 0, 5, 0));

        // Create UNISON control as numeric display with up/down triangles
        VBox unisonBox = createUnisonControl();

        VBox detuneBox = createKnobWithLabel(
                MAIN_KNOB_SIZE,
                Color.WHITE,
                "DETUNE",
                settings.getDetune(),
                0.0,
                1.0,
                newValue -> settings.setDetune(newValue.doubleValue()));

        VBox blendBox = createKnobWithLabel(
                MAIN_KNOB_SIZE,
                Color.WHITE,
                "LEVEL",
                settings.getLevel(),
                0.0,
                1.0,
                newValue -> settings.setLevel(newValue.doubleValue()));

        VBox phaseBox = createKnobWithLabel(
                MAIN_KNOB_SIZE,
                Color.WHITE,
                "PHASE",
                settings.getPhase(),
                0.0,
                1.0,
                newValue -> {
                    settings.setPhase(newValue.doubleValue());
                    redrawWaveform();
                });

        mainControlBox.getChildren().addAll(unisonBox, detuneBox, blendBox, phaseBox);
    }

    private VBox createUnisonControl() {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // Create a display rectangle for unison value
        StackPane displayPane = new StackPane();
        Rectangle displayBg = new Rectangle(30, 30); // Smaller to match the reduced knob size
        displayBg.setFill(Color.rgb(30, 30, 30));
        displayBg.setStroke(Color.rgb(60, 60, 60));
        displayBg.setStrokeWidth(1);
        displayBg.setArcWidth(4);
        displayBg.setArcHeight(4);

        // Add shadow to display
        DropShadow displayShadow = new DropShadow();
        displayShadow.setRadius(2);
        displayShadow.setOffsetX(1);
        displayShadow.setOffsetY(1);
        displayShadow.setColor(Color.rgb(0, 0, 0, 0.5));
        displayBg.setEffect(displayShadow);

        // Text for displaying the unison value
        Text valueText = new Text(String.valueOf(settings.getUnison()));
        valueText.setFill(Color.rgb(255, 160, 0)); // Orange-yellow color like in the image
        valueText.setFont(Font.font("Consolas", FontWeight.BOLD, 12)); // Smaller font
        valueText.setTextAlignment(TextAlignment.CENTER);

        // Create up/down triangle controls
        HBox trianglesBox = new HBox(2);
        trianglesBox.setAlignment(Pos.CENTER);

        // UP triangle
        Polygon upTriangle = new Polygon();
        upTriangle.getPoints().addAll(
                0.0, 8.0,
                8.0, 0.0,
                16.0, 8.0
        );
        upTriangle.setFill(Color.rgb(120, 120, 120));
        upTriangle.setOnMouseClicked(e -> {
            int unisonVoices = settings.getUnison();
            if (unisonVoices < 16) {
                settings.increaseUnison();
                valueText.setText(String.valueOf(++unisonVoices));
            }
        });
        upTriangle.setOnMouseEntered(e -> upTriangle.setFill(Color.rgb(180, 180, 180)));
        upTriangle.setOnMouseExited(e -> upTriangle.setFill(Color.rgb(120, 120, 120)));

        // DOWN triangle
        Polygon downTriangle = new Polygon();
        downTriangle.getPoints().addAll(
                0.0, 0.0,
                8.0, 8.0,
                16.0, 0.0
        );
        downTriangle.setFill(Color.rgb(120, 120, 120));
        downTriangle.setOnMouseClicked(e -> {
            int unisonVoices = settings.getUnison();
            if (unisonVoices > 1) {
                settings.decreaseUnison();
                valueText.setText(String.valueOf(--unisonVoices));
            }
        });
        downTriangle.setOnMouseEntered(e -> downTriangle.setFill(Color.rgb(180, 180, 180)));
        downTriangle.setOnMouseExited(e -> downTriangle.setFill(Color.rgb(120, 120, 120)));

        // Add triangles to vertical layout
        VBox trianglesVBox = new VBox(1);
        trianglesVBox.getChildren().addAll(upTriangle, downTriangle);
        trianglesVBox.setAlignment(Pos.CENTER);

        // Arrange triangles to the right of the display
        displayPane.getChildren().addAll(displayBg, valueText);

        // Add the display pane and triangles to a horizontal layout
        HBox displayWithTriangles = new HBox(3);
        displayWithTriangles.setAlignment(Pos.CENTER);
        displayWithTriangles.getChildren().addAll(displayPane, trianglesVBox);

        // Create the label
        Label label = new Label("UNISON");
        label.setTextFill(Color.rgb(180, 180, 180));
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 10)); // Smaller font

        box.getChildren().addAll(displayWithTriangles, label);
        return box;
    }

    private VBox createKnobWithLabel(int size, Color color, String labelText, double initialValue, double minValue, double maxValue,
                                     Consumer<Number> listener) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        // Create the knob with white indicator
        KnobControl knob = new KnobControl(size, color,
                initialValue,
                minValue,
                maxValue);

        // Create the label
        Label label = new Label(labelText);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 10)); // Smaller font

        knob.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) {
                listener.accept(newVal);
            }
        });

        box.getChildren().addAll(knob, label);
        return box;
    }

    private void setupEnvelopeControls() {
        envelopeControlBox.getChildren().clear();

        envelopeControlBox.setSpacing(10);
        envelopeControlBox.setAlignment(Pos.CENTER);
        envelopeControlBox.setPadding(new Insets(5, 0, 5, 0));

        VBox attackBox = createKnobWithLabel(
                ADSR_KNOB_SIZE,
                ATTACK_KNOB_COLOR,
                "A",
                settings.getAttack(),
                0.0,
                ATTACK_TIME_MAX_VALUE,
                newValue -> {
                    settings.setAttack(newValue.doubleValue());
                    drawEnvelope();
                });
        VBox decayBox = createKnobWithLabel(
                ADSR_KNOB_SIZE,
                DECAY_KNOB_COLOR,
                "D",
                settings.getDecay(),
                0.0,
                DECAY_TIME_MAX_VALUE,
                newValue -> {
                    settings.setDecay(newValue.doubleValue());
                    drawEnvelope();
                });
        VBox sustainBox = createKnobWithLabel(
                ADSR_KNOB_SIZE,
                SUSTAIN_KNOB_COLOR,
                "S",
                settings.getSustain(),
                0.0,
                1.0,
                newValue -> {
                    settings.setSustain(newValue.doubleValue());
                    drawEnvelope();
                });
        VBox releaseBox = createKnobWithLabel(
                ADSR_KNOB_SIZE,
                RELEASE_KNOB_COLOR,
                "R",
                settings.getRelease(),
                0.0,
                RELEASE_TIME_MAX_VALUE,
                newValue -> {
                    settings.setRelease(newValue.doubleValue());
                    drawEnvelope();
                });

        envelopeControlBox.getChildren().addAll(attackBox, decayBox, sustainBox, releaseBox);
    }

    private void drawEnvelope() {
        // Get the canvas dimensions
        double width = envelopeCanvas.getWidth();
        double height = envelopeCanvas.getHeight();

        // Define margins
        double margin = 10;

        GraphicsContext gc = envelopeCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        // Fill background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Calculate envelope dimensions
        double graphWidth = width - 2 * margin;
        double graphHeight = height - 2 * margin;

        // Calculate fixed segment widths - each segment takes max 1/4 of the space when at maximum
        double maxSegmentWidth = graphWidth / 4;

        // Calculate normalized segment widths
        double attackWidth = maxSegmentWidth * (settings.getAttack() / ATTACK_TIME_MAX_VALUE);
        double decayWidth = maxSegmentWidth * (settings.getDecay() / DECAY_TIME_MAX_VALUE);
        double sustainWidth = maxSegmentWidth; // Always 1/4 of width
        double releaseWidth = maxSegmentWidth * (settings.getRelease() / RELEASE_TIME_MAX_VALUE);

        // If a segment is set to 0, still draw a tiny line so it's visible
        double minWidth = 1;
        if (attackWidth < minWidth) attackWidth = minWidth;
        if (decayWidth < minWidth) decayWidth = minWidth;
        if (releaseWidth < minWidth) releaseWidth = minWidth;

        // Calculate points
        double x1 = margin; // Start
        double y1 = height - margin; // Bottom

        double x2 = x1 + attackWidth; // End of attack
        double y2 = margin; // Top

        double x3 = x2 + decayWidth; // End of decay
        double y3 = margin + graphHeight * (1 - settings.getSustain()); // Sustain level

        double x4 = x3 + sustainWidth; // End of sustain
        double y4 = y3; // Same level as decay endpoint

        double x5 = x4 + releaseWidth; // End of release
        double y5 = height - margin; // Bottom again

        // Draw axes (light gray)
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeLine(margin, height - margin, width - margin, height - margin); // X-axis
        gc.strokeLine(margin, margin, margin, height - margin); // Y-axis

        // Draw segments with their colors
        gc.setLineWidth(2);

        // Attack segment (red)
        gc.setStroke(ATTACK_KNOB_COLOR);
        gc.strokeLine(x1, y1, x2, y2);

        // Decay segment (cyan)
        gc.setStroke(DECAY_KNOB_COLOR);
        gc.strokeLine(x2, y2, x3, y3);

        // Sustain segment (yellow)
        gc.setStroke(SUSTAIN_KNOB_COLOR);
        gc.strokeLine(x3, y3, x4, y4);

        // Release segment (magenta)
        gc.setStroke(RELEASE_KNOB_COLOR);
        gc.strokeLine(x4, y4, x5, y5);

        // Draw points at segment joints
        gc.setFill(Color.WHITE);
        drawPoint(gc, x1, y1);
        drawPoint(gc, x2, y2);
        drawPoint(gc, x3, y3);
        drawPoint(gc, x4, y4);
        drawPoint(gc, x5, y5);

        // Draw ADSR label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14)); // Slightly smaller
        gc.fillText("ADSR", width - 45, 20);
    }

    private void drawPoint(GraphicsContext gc, double x, double y) {
        gc.fillOval(x - 2, y - 2, 4, 4); // Keep same size for visibility
    }

    private void redrawWaveform() {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();

        // Очистка канваса чёрным фоном
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Рисуем едва заметную горизонтальную линию в середине,
        // которая обозначает ось X (нулевой уровень амплитуды)
        gc.setStroke(Color.rgb(255, 255, 255, 0.4));  // белый с 20% непрозрачности
        gc.setLineWidth(1.0);
        gc.strokeLine(0, height / 2, width, height / 2);

        // Настройка цвета и ширины линии для отрисовки волны – салатовый (LawnGreen)
        gc.setStroke(Color.LAWNGREEN);
        gc.setLineWidth(2.0);

        // Количество периодов, которые нужно отрисовать – в данном случае 2 (две фазы)
        double cycles = 2.0;
        gc.beginPath();
        for (int x = 0; x < width; x++) {
            // Рассчитываем текущую фазу от phaseAmount до phaseAmount + cycles
            double currentPhase = settings.getPhase() + (cycles * x) / width;
            // Приведение фазы к диапазону [0, 1), так как generateWaveform ожидает нормализованное значение
            double normalizedPhase = currentPhase % 1.0;
            float amplitude = generateWaveform(settings.getWaveform(), normalizedPhase) * 0.8f;
            // Преобразование амплитуды из диапазона [-1, 1] в координату Y на канвасе
            double y = height / 2 - amplitude * (height / 2);

            if (x == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }
        gc.stroke();
    }
}
