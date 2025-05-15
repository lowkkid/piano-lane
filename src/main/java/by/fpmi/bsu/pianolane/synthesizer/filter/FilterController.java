package by.fpmi.bsu.pianolane.synthesizer.filter;

import static by.fpmi.bsu.pianolane.common.util.enums.FilterType.BAND_PASS;

import by.fpmi.bsu.pianolane.common.util.enums.FilterType;
import by.fpmi.bsu.pianolane.common.ui.ElementsFactory;
import java.util.Arrays;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilterController {

    @FXML private VBox filterRoot;
    @FXML private HBox filterControlBox;
    @FXML private ChoiceBox<String> filterTypeChoiceBox;
    @FXML private CheckBox filterEnableButton;
    @FXML private Label frequencyValueLabel;
    @FXML private Label resonanceValueLabel;
    @FXML private Canvas responseCanvas;
    @FXML private HBox resonanceKnobContainer;
    @FXML private HBox frequencyKnobContainer;

    private final FilterSettings filterSettings;

    private GraphicsContext gc;

    public void initialize() {
        gc = responseCanvas.getGraphicsContext2D();

        filterTypeChoiceBox.getItems().addAll(
                Arrays.stream(FilterType.values()).map(FilterType::getDisplayName).toList()
        );
        filterTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    boolean isBandPass = BAND_PASS.equals(newVal);
                    resonanceKnobContainer.setVisible(isBandPass);
                    resonanceKnobContainer.setManaged(isBandPass);

                    filterSettings.setFilterType(FilterType.fromDisplayName(newVal));
                    updateFilterVisualization();
                });
        filterTypeChoiceBox.setValue(filterSettings.getFilterType().getDisplayName());




        updateFilterVisualization();

        filterEnableButton.setOnAction(event -> {
            boolean locked = !filterEnableButton.isSelected();
            switchFilterPanel(locked);
            filterSettings.setEnabled(filterEnableButton.isSelected());
        });
        switchFilterPanel(!filterSettings.isEnabled());
        filterEnableButton.setSelected(filterSettings.isEnabled());


        initializeControls();
    }

    private void initializeControls() {
        VBox frequencyKnob = ElementsFactory.createKnobWithLabel(
                20,
                Color.WHITE,
                "FREQUENCY",
                filterSettings.getFrequency(),
                20.0,
                20000.0,
                newValue -> {
                    double frequency = mapToLogarithmic(newValue.doubleValue(), 20, 20000);
                    filterSettings.setFrequency(frequency);
                    frequencyValueLabel.setText(String.format("%.0f Hz", frequency));
                    updateFilterVisualization();
                });
        frequencyKnobContainer.getChildren().add(frequencyKnob);

        VBox resonanceKnob = ElementsFactory.createKnobWithLabel(
                20,
                Color.WHITE,
                "RESONANCE",
                filterSettings.getQ(),
                0.1,
                20.0,
                newValue -> {
                    filterSettings.setQ(newValue.doubleValue());
                    resonanceValueLabel.setText(String.format("%.1f", newValue.doubleValue()));
                    updateFilterVisualization();
                });
        resonanceKnobContainer.getChildren().add(resonanceKnob);
    }

    private void switchFilterPanel(boolean locked) {
        filterTypeChoiceBox.setDisable(locked);
        filterControlBox.setDisable(locked);

        if (locked) {
            if (!filterRoot.getStyleClass().contains("disabled-panel")) {
                filterRoot.getStyleClass().add("disabled-panel");
            }
        } else {
            filterRoot.getStyleClass().remove("disabled-panel");
        }
    }

    private void updateFilterVisualization() {
        gc.clearRect(0, 0, responseCanvas.getWidth(), responseCanvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, responseCanvas.getWidth(), responseCanvas.getHeight());
        drawGrid();
        drawResponseCurve(filterSettings.getFilterType(), filterSettings.getFrequency(), filterSettings.getQ());
    }

    private void drawGrid() {
        gc.setStroke(Color.rgb(40, 40, 40));
        gc.setLineWidth(0.5);

        for (int i = 0; i <= 4; i++) {
            double y = i * (responseCanvas.getHeight() / 4);
            gc.strokeLine(0, y, responseCanvas.getWidth(), y);
        }

        double[] freqs = {100, 1000, 10000};
        for (double freq : freqs) {
            double x = mapFrequencyToX(freq);
            gc.strokeLine(x, 0, x, responseCanvas.getHeight());
        }
    }

    private void drawResponseCurve(FilterType filterType, double frequency, double q) {
        gc.setStroke(Color.rgb(0, 170, 255));
        gc.setLineWidth(2);

        double prevX = 0;
        double prevY = 0;
        boolean first = true;

        // Draw 100 points across the frequency spectrum
        for (int i = 0; i <= 100; i++) {
            // Map i to a frequency between 20Hz and 20kHz (logarithmic)
            double freq = 20 * Math.pow(1000, i / 100.0);
            double response = calculateFilterResponse(filterType, freq, frequency, q);

            double x = i * responseCanvas.getWidth() / 100;
            double y = mapResponseToY(response);

            if (first) {
                first = false;
            } else {
                gc.strokeLine(prevX, prevY, x, y);
            }

            prevX = x;
            prevY = y;
        }
    }

    private double calculateFilterResponse(FilterType filterType, double freq, double cutoff, double q) {
        double w = freq / cutoff;

        return switch (filterType) {
            case LOW_PASS_FS -> 20 * Math.log10(1.0 / Math.sqrt(1.0 + Math.pow(w, 8)));
            case LOW_PASS_SP -> 20 * Math.log10(1.0 / Math.sqrt(1.0 + Math.pow(w, 2)));
            case HIGH_PASS -> 20 * Math.log10(w / Math.sqrt(1.0 + Math.pow(w, 2)));
            case BAND_PASS -> {
                double bandwidth = cutoff / q;
                double bw = bandwidth / cutoff;
                double term = freq / cutoff - cutoff / freq;
                yield 20 * Math.log10(bw / Math.sqrt(term * term + bw * bw));
            }
        };
    }

    private double mapFrequencyToX(double freq) {
        // Map frequency (20Hz-20kHz) to x coordinate (logarithmic)
        double logFreq = Math.log10(freq / 20) / Math.log10(1000);
        return logFreq * responseCanvas.getWidth();
    }

    private double mapResponseToY(double response) {
        // Map response (-24dB to +24dB) to y coordinate
        double range = 48;
        double normalized = (response + 24) / range;
        return responseCanvas.getHeight() * (1 - normalized);
    }

    private double mapToLogarithmic(double value, double min, double max) {
        double minLog = Math.log(min);
        double maxLog = Math.log(max);
        double scale = (maxLog - minLog) / (max - min);
        return Math.exp(minLog + scale * (value - min));
    }
}