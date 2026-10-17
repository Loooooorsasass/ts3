package com.example.game.ui.template

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/*
 * TEMPLATE — number that counts to its new value.
 *  - `value` must already be the authoritative final value (do not let this own game state).
 *  - Recomposes each frame while animating; use for a few counters, not for long lists.
 *  - Precision: Float is exact up to about 16.7 million; use a Double/Long-based animation for larger values.
 *  - Accessibility: TalkBack reads the final formatted value immediately.
 *  - "tnum" works only if the font provides tabular figures.
 */
@Composable
fun CountUpText(
    value: Long,
    modifier: Modifier = Modifier,
    style: TextStyle,
    durationMillis: Int = 600,
    reduceMotion: Boolean = rememberReduceMotion(),
    formatter: (Long) -> String = { GameNumberFormat.compact(it) },
) {
    val animated = remember { Animatable(value.toFloat()) }
    LaunchedEffect(value, reduceMotion) {
        if (reduceMotion) {
            animated.snapTo(value.toFloat())
        } else {
            animated.animateTo(
                targetValue = value.toFloat(),
                animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
            )
        }
    }
    val finalDescription = formatter(value)
    BasicText(
        text = formatter(animated.value.toLong()),
        style = style.copy(fontFeatureSettings = "tnum"),
        modifier = modifier.semantics { contentDescription = finalDescription },
    )
}

object GameNumberFormat {
    /**
     * 0..9,999 -> full number with locale grouping; then 10K, 1.2M, 3.4B (decimal separator follows the locale).
     * Rounds down so a value is never overstated. Suffix letters are a product decision —
     * localize them through the project's string system if needed.
     */
    fun compact(value: Long, locale: Locale = Locale.getDefault()): String = when {
        value >= 1_000_000_000L -> scaled(value / 1_000_000_000.0, "B", locale)
        value >= 1_000_000L -> scaled(value / 1_000_000.0, "M", locale)
        value >= 10_000L -> scaled(value / 1_000.0, "K", locale)
        else -> NumberFormat.getIntegerInstance(locale).format(value)
    }

    private fun scaled(v: Double, suffix: String, locale: Locale): String {
        val nf = NumberFormat.getNumberInstance(locale).apply {
            maximumFractionDigits = 1
            minimumFractionDigits = 0
            roundingMode = RoundingMode.DOWN
        }
        return nf.format(v) + suffix
    }
}
