package by.fpmi.bsu.pianolane.util;

import javafx.scene.shape.Rectangle;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyUtil {

    public static Rectangle copy(Rectangle original) {
        Rectangle copy = new Rectangle();
        copy.setX(original.getX());
        copy.setY(original.getY());
        copy.setWidth(original.getWidth());
        copy.setHeight(original.getHeight());
        copy.setFill(original.getFill());
        copy.setStroke(original.getStroke());
        copy.setStrokeWidth(original.getStrokeWidth());
        copy.setArcWidth(original.getArcWidth());
        copy.setArcHeight(original.getArcHeight());
        return copy;
    }
}
