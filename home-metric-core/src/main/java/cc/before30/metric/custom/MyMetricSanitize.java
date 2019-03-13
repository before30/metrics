package cc.before30.metric.custom;

import java.util.regex.Pattern;

public class MyMetricSanitize {
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final String DASH = "-";

    static String sanitize(String string) {
        return WHITESPACE.matcher(string.trim()).replaceAll(DASH);
    }
}
