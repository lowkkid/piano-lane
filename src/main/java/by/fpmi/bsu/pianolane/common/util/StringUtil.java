package by.fpmi.bsu.pianolane.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    private static final int COMPACT_STRING_LENGTH = 10;
    private static final int END_OF_COMPACT_STRING_LENGTH = 4;
    private static final String SEPARATOR = "...";

    public static String toCompactString(String str) {
        if (str.length() <= COMPACT_STRING_LENGTH) {
            return str;
        }
        int startOfCompactStringLength = COMPACT_STRING_LENGTH - END_OF_COMPACT_STRING_LENGTH - SEPARATOR.length();
        String startOfCompactString = str.substring(0, startOfCompactStringLength);
        String endOfCompactString = str.substring(str.length() - END_OF_COMPACT_STRING_LENGTH);
        return startOfCompactString + SEPARATOR + endOfCompactString;
    }

    public static void main(String[] args) {
        System.out.println(toCompactString("custom synth"));
    }
}
