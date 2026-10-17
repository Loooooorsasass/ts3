package com.example.game.ui.template

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/*
 * TEMPLATE — game-style button with a "lip" that collapses when pressed.
 *
 * Adapt before use:
 *  - Replace GameButtonDefaults colors/typography with project tokens (see references/10-style-presets.md).
 *  - Pass localized strings from the project's string system; never hard-code player-facing text.
 *  - No Material dependency is required (BasicText + foundation only).
 *  - Many buttons on one screen: consider drawWithCache to avoid allocating a Brush per draw.
 *
 * Behavior: min 56 dp face height (plus lip), whole area is the touch target, TalkBack reads the
 * label and "Button" role, disabled state is exposed via clickable(enabled = false).
 */
@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tokens: GameSurfaceTokens = GameButtonDefaults.primary,
    textStyle: TextStyle = GameButtonDefaults.textStyle,
    enabled: Boolean = true,
    reduceMotion: Boolean = rememberReduceMotion(),
    // Opt in with a feedback type supported by the installed Compose version and product semantics.
    hapticType: HapticFeedbackType? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val colors = remember(tokens, enabled) { if (enabled) tokens else tokens.asDisabled() }
    val source = remember(interactionSource) { interactionSource ?: MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val focused by source.collectIsFocusedAsState()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current
    val depthPx = with(density) { colors.depth.toPx() }
    val pressShiftPx = depthPx * 0.8f

    val travel by animateFloatAsState(
        targetValue = if (pressed && enabled) 1f else 0f,
        animationSpec = if (reduceMotion) {
            snap()
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
            )
        },
        label = "gameButtonTravel",
    )

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 88.dp, minHeight = 56.dp + colors.depth)
            .clickable(
                interactionSource = source,
                indication = null, // pressed feedback is the surface travel; focus is drawn below
                enabled = enabled,
                role = Role.Button,
            ) {
                hapticType?.let { haptics.performHapticFeedback(it) }
                onClick()
            }
            .drawBehind { drawGameSurface(colors, travel, depthPx, pressShiftPx, focused && enabled) }
            .padding(bottom = colors.depth)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.offset { IntOffset(0, (travel * pressShiftPx).roundToInt()) },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            BasicText(
                text = text,
                style = textStyle.copy(color = colors.onSurface),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun DrawScope.drawGameSurface(
    t: GameSurfaceTokens,
    travel: Float,
    depthPx: Float,
    shiftPx: Float,
    focused: Boolean,
) {
    val radius = CornerRadius(t.cornerRadius.toPx())
    val faceHeight = size.height - depthPx
    val shift = travel * shiftPx
    val edgePx = t.edgeWidth.toPx()

    // 1) Lip: the visible "thickness" under the face.
    drawRoundRect(
        color = t.lip,
        topLeft = Offset(0f, depthPx),
        size = Size(size.width, faceHeight),
        cornerRadius = radius,
    )

    // 2) Face: subtle vertical gradient, light from the top.
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(t.faceTop, t.faceBottom),
            startY = shift,
            endY = shift + faceHeight,
        ),
        topLeft = Offset(0f, shift),
        size = Size(size.width, faceHeight),
        cornerRadius = radius,
    )

    // 3) Edge stroke, inset by half its width so it stays inside the bounds.
    drawRoundRect(
        color = t.edge,
        topLeft = Offset(edgePx / 2f, shift + edgePx / 2f),
        size = Size(size.width - edgePx, faceHeight - edgePx),
        cornerRadius = radius,
        style = Stroke(width = edgePx),
    )

    // 4) Top highlight: a thin light line just inside the upper edge.
    val inset = edgePx + 2.dp.toPx()
    val sideInset = radius.x * 0.8f
    drawLine(
        color = t.highlight,
        start = Offset(sideInset, shift + inset),
        end = Offset(size.width - sideInset, shift + inset),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
    )
    // 5) Keyboard/switch focus ring. It is drawn along the outer edge of the button's bounds (over
    //    the edge stroke) and is not shifted by the press, so it stays put while the face travels.
    //    The color is the `focusRing` token: check it against the edge stroke AND the screen background.
    if (focused) {
        val ring = 2.dp.toPx()
        drawRoundRect(
            color = t.focusRing,
            topLeft = Offset(ring / 2f, ring / 2f),
            size = Size(size.width - ring, size.height - ring),
            cornerRadius = radius,
            style = Stroke(width = ring),
        )
    }
}

/** PLACEHOLDER values (Candy Cartoon example). Replace with the project's tokens. */
object GameButtonDefaults {
    val primary = GameSurfaceTokens(
        faceTop = Color(0xFFFFC94A),
        faceBottom = Color(0xFFFF9F1C),
        edge = Color(0xFFA85A00),
        lip = Color(0xFF7A3F00),
        highlight = Color(0x99FFFFFF),
        onSurface = Color(0xFF3A1F00),
    )
    val secondary = GameSurfaceTokens(
        faceTop = Color(0xFF5663E8),
        faceBottom = Color(0xFF3B47C9),
        edge = Color(0xFF232C8F),
        lip = Color(0xFF181F66),
        highlight = Color(0x66FFFFFF),
        onSurface = Color(0xFFFFFFFF),
    )
    val danger = GameSurfaceTokens(
        faceTop = Color(0xFFD2354D),
        faceBottom = Color(0xFFB8243C),
        edge = Color(0xFF7D1428),
        lip = Color(0xFF560C1B),
        highlight = Color(0x66FFFFFF),
        onSurface = Color(0xFFFFFFFF),
    )
    val textStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp, // leave room for stacked diacritics (for example Vietnamese)
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
    )
}
