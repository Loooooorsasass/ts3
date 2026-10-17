package com.example.game.ui.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * TEMPLATE — outlined text for busy game backgrounds.
 * Works on any Compose version: eight offset copies in the outline color sit under the fill.
 * Cost: nine text layouts per instance — use for titles and key numbers, not long lists.
 * The outline copies are hidden from accessibility so TalkBack reads the text once.
 * An outline does not count toward WCAG contrast; the fill color must stay readable on its own.
 */
@Composable
fun OutlinedGameText(
    text: String,
    style: TextStyle,
    fillColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
    outlineWidth: Dp = 2.dp,
) {
    // Offsets for 8 directions. Diagonals use 0.7071 so every copy sits at the same radius.
    val d = outlineWidth
    val k = 0.7071f
    val offsets = listOf(
        d to 0.dp, -d to 0.dp, 0.dp to d, 0.dp to -d,
        d * k to d * k, -d * k to d * k, d * k to -d * k, -d * k to -d * k,
    )
    Box(modifier) {
        offsets.forEach { (dx, dy) ->
            BasicText(
                text = text,
                style = style.copy(color = outlineColor),
                modifier = Modifier
                    .offset(x = dx, y = dy)
                    .clearAndSetSemantics { },
            )
        }
        BasicText(text = text, style = style.copy(color = fillColor))
    }
}
