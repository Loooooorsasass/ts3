package com.example.game.ui.template

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * TEMPLATE — starting point, NOT drop-in production code.
 * Before using: change the package, map these tokens onto the project's existing theme/tokens,
 * check the installed Compose version, and compile. Placeholder colors come from the
 * "Candy Cartoon" example in references/10-style-presets.md.
 */

/** Colors and metrics for one "physical" surface: face gradient, edge, lip (base shadow), highlight. */
@Immutable
data class GameSurfaceTokens(
    val faceTop: Color,
    val faceBottom: Color,
    val edge: Color,
    val lip: Color,
    val highlight: Color,
    val onSurface: Color,
    val cornerRadius: Dp = 16.dp,
    val edgeWidth: Dp = 2.dp,
    val depth: Dp = 5.dp,
    /** Keyboard/switch focus ring. Check it against both the edge stroke and the screen background. */
    val focusRing: Color = Color.White,
)

private val DisabledTint = Color(0xFFAAA6AF)

/** Desaturated version for disabled controls. Keeps the label readable; shape is unchanged. */
fun GameSurfaceTokens.asDisabled(): GameSurfaceTokens = copy(
    faceTop = lerp(faceTop, DisabledTint, 0.65f),
    faceBottom = lerp(faceBottom, DisabledTint, 0.65f),
    edge = lerp(edge, DisabledTint, 0.65f),
    lip = lerp(lip, DisabledTint, 0.75f),
    highlight = highlight.copy(alpha = highlight.alpha * 0.5f),
    // Keep the label dark instead of desaturating it into low contrast on the lighter disabled face.
    onSurface = Color(0xFF211D24),
)
