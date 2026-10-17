package com.example.game.ui.template

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
 * TEMPLATE — segmented progress/health bar with a "damage trail".
 *  - `fraction` is the authoritative value (0..1) from the state owner; this composable only renders it.
 *  - The LAST target always wins: if values change rapidly the bar converges to the latest one.
 *  - Do not rely on color alone: pair with a numeric label and/or a low-state icon (see references/08).
 *  - Give the caller a size, for example Modifier.fillMaxWidth().height(18.dp).
 *  - The animation state lives in `BarAnimation` / `rememberBarAnimation` (internal) so tests can
 *    check the real front and trail values. Accessibility semantics intentionally report the
 *    target value only, not the per-frame animated value.
 */
@Immutable
data class SegmentedBarColors(
    val track: Color,
    val trail: Color,
    val fill: Color,
    val low: Color,
)

@Composable
fun SegmentedBar(
    fraction: Float,
    label: String,
    modifier: Modifier = Modifier,
    segments: Int = 10,
    colors: SegmentedBarColors = SegmentedBarDefaults.health,
    lowThreshold: Float = 0.25f,
    /** Localized state spoken by accessibility services, e.g. "Low health" or "Normal health". */
    stateDescription: String? = null,
    reduceMotion: Boolean = rememberReduceMotion(),
) {
    val target = fraction.coerceIn(0f, 1f)
    val animation = rememberBarAnimation(target, reduceMotion)
    val front = animation.front
    val ghost = animation.ghost

    Canvas(
        modifier = modifier.semantics {
            contentDescription = label
            progressBarRangeInfo = ProgressBarRangeInfo(target, 0f..1f)
            if (stateDescription != null) this.stateDescription = stateDescription
        },
    ) {
        val n = segments.coerceAtLeast(1)
        val gap = 3.dp.toPx()
        val segW = (size.width - gap * (n - 1)) / n
        val corner = CornerRadius(size.height * 0.3f)
        val fillColor = if (target <= lowThreshold) colors.low else colors.fill
        val h = size.height

        for (i in 0 until n) {
            val x = i * (segW + gap)
            val segSize = Size(segW, h)
            drawRoundRect(colors.track, Offset(x, 0f), segSize, corner)

            val ghostFill = (ghost.value * n - i).coerceIn(0f, 1f)
            if (ghostFill > 0f) {
                clipRect(left = x, top = 0f, right = x + segW * ghostFill, bottom = h) {
                    drawRoundRect(colors.trail, Offset(x, 0f), segSize, corner)
                }
            }
            val frontFill = (front.value * n - i).coerceIn(0f, 1f)
            if (frontFill > 0f) {
                clipRect(left = x, top = 0f, right = x + segW * frontFill, bottom = h) {
                    drawRoundRect(fillColor, Offset(x, 0f), segSize, corner)
                }
            }
        }
    }
}

/**
 * Animated values behind [SegmentedBar]: `front` is the fill, `ghost` is the damage trail.
 * Internal so that androidTest in the same module can assert on the real animated values.
 */
internal class BarAnimation(initial: Float) {
    val front = Animatable(initial)
    val ghost = Animatable(initial)
}

/**
 * Drives [BarAnimation] toward [target]. The LAST target wins: a new target cancels the running
 * effect, and the next animation starts from the current animated values.
 */
@Composable
internal fun rememberBarAnimation(target: Float, reduceMotion: Boolean): BarAnimation {
    val animation = remember { BarAnimation(target) }
    LaunchedEffect(target, reduceMotion) {
        val front = animation.front
        val ghost = animation.ghost
        if (reduceMotion) {
            front.snapTo(target)
            ghost.snapTo(target)
            return@LaunchedEffect
        }
        if (target < front.value) {
            // Damage: front drops fast, the trail waits and then catches up.
            launch { front.animateTo(target, tween(120)) }
            delay(350)
            ghost.animateTo(target, tween(450))
        } else {
            // Heal/progress: both rise together so no stale trail is visible.
            launch { ghost.animateTo(target, tween(300)) }
            front.animateTo(target, tween(300))
        }
    }
    return animation
}

/** PLACEHOLDER values. Replace with project tokens. */
object SegmentedBarDefaults {
    val health = SegmentedBarColors(
        track = Color(0xFF1B1D4A),
        trail = Color(0xFFFFE7A3),
        fill = Color(0xFF5CD65C),
        low = Color(0xFFFF5468),
    )
}
