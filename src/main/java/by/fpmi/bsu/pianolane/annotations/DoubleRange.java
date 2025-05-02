package by.fpmi.bsu.pianolane.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DoubleRange {

    double min() default Double.MIN_VALUE;
    double max() default Double.MAX_VALUE;
    String message() default "Value of parameter %s must be between %s and %s";
}
