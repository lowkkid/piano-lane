package by.fpmi.bsu.pianolane.synthesizer.filter;

import by.fpmi.bsu.pianolane.common.util.enums.FilterType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class FilterSettings {

    private boolean isEnabled = false;
    private FilterType filterType = FilterType.LOW_PASS_SP;
    private double frequency = 20;
    private double q = 1;
}
