package com.example.game.ui.template.views;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * TEMPLATE. 0..9,999 -> full number with locale grouping; then 10K, 1.2M, 3.4B.
 * Rounds down so a value is never overstated. Suffix letters are a product decision —
 * localize them through the project's string system if needed.
 */
public final class GameNumberFormat {
    private GameNumberFormat() {}

    public static String compact(long value, Locale locale) {
        if (value >= 1_000_000_000L) return scaled(value / 1_000_000_000.0, "B", locale);
        if (value >= 1_000_000L) return scaled(value / 1_000_000.0, "M", locale);
        if (value >= 10_000L) return scaled(value / 1_000.0, "K", locale);
        return NumberFormat.getIntegerInstance(locale).format(value);
    }

    private static String scaled(double v, String suffix, Locale locale) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(0);
        nf.setRoundingMode(RoundingMode.DOWN);
        return nf.format(v) + suffix;
    }
}
