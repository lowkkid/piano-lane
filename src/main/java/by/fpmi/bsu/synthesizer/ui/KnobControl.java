package by.fpmi.bsu.synthesizer.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class KnobControl extends StackPane {

    private static final int MIN_ANGLE = 45;
    private static final int MAX_ANGLE = 315;
    private static final int DELTA_ANGLE = MAX_ANGLE - MIN_ANGLE;

    private final Circle knobCircle;
    private final Circle outerRing;
    private final Line indicator;
    private final Color color;


    private final DoubleProperty value = new SimpleDoubleProperty();
    private final double minValue;
    private final double maxValue;

    public KnobControl(double size, Color color, double initialValue, double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.color = color;

        outerRing = new Circle(size);
        styleOuterRing();

        knobCircle = new Circle(size - 4);
        styleKnobCircle();


        double indicatorLength = size - 2;
        indicator = new Line(0, 0, 0, -indicatorLength);
        indicator.setStrokeWidth(1.5);
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1,
                true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.5, Color.TRANSPARENT),
                new Stop(0.5, color),
                new Stop(1, color)
        );
        indicator.setStroke(gradient);

        value.set(initialValue);
        indicator.setRotate(valueToAngle(initialValue));

        setOnScroll(this::handleScroll);

        getChildren().addAll(outerRing, knobCircle, indicator);
        setPrefSize(size * 2, size * 2);
    }

    private void handleScroll(ScrollEvent event) {
        double deltaY = event.getDeltaY();

        double sensitivity = 0.3;
        double deltaDegrees = deltaY * sensitivity;
        double newRotation = indicator.getRotate() + deltaDegrees;

        newRotation = Math.max(MIN_ANGLE, Math.min(MAX_ANGLE, newRotation));

        indicator.setRotate(newRotation);

        value.set(angleToValue(newRotation));
        event.consume();
    }

    /**
     * Converts double value to angle
     * @param val between {@link #minValue} and {@link #maxValue}
     * @return angle from {@link #MIN_ANGLE} to {@link #MAX_ANGLE} degrees
     */
    private double valueToAngle(double val) {
        return MIN_ANGLE + ((val - minValue) / (maxValue - minValue)) * DELTA_ANGLE;
    }

    /**
     * Converts angle to double value
     * @param angle from {@link #MIN_ANGLE} to {@link #MAX_ANGLE} degrees
     * @return value between {@link #minValue} and {@link #maxValue}
     */
    private double angleToValue(double angle) {
        return minValue + ((angle - MIN_ANGLE) / DELTA_ANGLE) * (maxValue - minValue);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    private void styleOuterRing() {
        Stop[] outerStops = new Stop[] {
                new Stop(0.8, Color.rgb(70, 70, 70)),
                new Stop(0.9, Color.rgb(40, 40, 40)),
                new Stop(0.95, Color.rgb(70, 70, 70)),
                new Stop(1.0, Color.rgb(30, 30, 30))
        };

        RadialGradient outerGradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1.0, true, CycleMethod.NO_CYCLE, outerStops
        );

        outerRing.setFill(outerGradient);
        outerRing.setStroke(color);
        outerRing.setStrokeWidth(1.5);

        DropShadow outerShadow = new DropShadow();
        outerShadow.setRadius(3.5);
        outerShadow.setOffsetX(1.0);
        outerShadow.setOffsetY(1.0);
        outerShadow.setColor(Color.rgb(0, 0, 0, 0.7));
        outerRing.setEffect(outerShadow);
    }

    private void styleKnobCircle() {
        Stop[] knobStops = new Stop[] {
                new Stop(0.0, Color.rgb(180, 180, 180)),
                new Stop(0.3, Color.rgb(100, 100, 100)),
                new Stop(0.7, Color.rgb(60, 60, 60)),
                new Stop(1.0, Color.rgb(30, 30, 30))
        };

        RadialGradient knobGradient = new RadialGradient(
                315, 0.8, 0.5, 0.3, 0.7, true, CycleMethod.NO_CYCLE, knobStops
        );

        knobCircle.setFill(knobGradient);

        Lighting lighting = new Lighting();
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);
        light.setElevation(30.0);
        lighting.setLight(light);
        lighting.setSurfaceScale(3.5);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(3.5);
        innerShadow.setChoke(0.2);
        innerShadow.setOffsetX(-1);
        innerShadow.setOffsetY(-1);
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.7));

        knobCircle.setEffect(innerShadow);
    }
}