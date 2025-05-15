package by.fpmi.bsu.pianolane.common.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterType {
    LOW_PASS_SP("Gentle low pass"),
    LOW_PASS_FS("Hard low pass"),
    HIGH_PASS("High pass"),
    BAND_PASS("Band pass");

    private final String displayName;

    public static FilterType fromDisplayName(String displayName) {
        for (var type : FilterType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown filter type: " + displayName);
    }

    public boolean equals(String displayName) {
        return this.displayName.equals(displayName);
    }
}
