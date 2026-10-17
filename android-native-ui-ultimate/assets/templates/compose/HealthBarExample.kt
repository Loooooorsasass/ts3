package com.example.game.ui.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Usage example, not a mandatory wrapper. Pass localized strings from stringResource()/your string
 * layer. The warning mark is intentionally separate from the bar so low health is not encoded by
 * color alone. The bar exposes its numeric range and caller-provided localized state description.
 */
@Composable
fun HealthBarExample(
    fraction: Float,
    healthLabel: String,
    normalStateDescription: String,
    lowStateDescription: String,
    lowWarningLabel: String,
    modifier: Modifier = Modifier,
    reduceMotion: Boolean = rememberReduceMotion(),
) {
    val value = fraction.coerceIn(0f, 1f)
    val isLow = value <= 0.25f

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (isLow) {
            BasicText(
                text = "!",
                style = TextStyle(
                    color = SegmentedBarDefaults.health.low,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .size(24.dp)
                    .clearAndSetSemantics { contentDescription = lowWarningLabel },
            )
        }
        SegmentedBar(
            fraction = value,
            label = healthLabel,
            stateDescription = if (isLow) lowStateDescription else normalStateDescription,
            modifier = Modifier.weight(1f),
            reduceMotion = reduceMotion,
        )
    }
}
