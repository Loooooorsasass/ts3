package com.example.game.ui.template

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/*
 * TEMPLATE — one-shot particle burst for reward moments.
 *  - Increase `trigger` (for example, a claim counter) to fire a burst; 0 or less = idle.
 *  - Deterministic: the same trigger value gives the same pattern (useful for screenshot tests).
 *  - No allocation per frame: particle data lives in primitive arrays.
 *  - Decorative only: no semantics. Show the reward as real text (for example "+50") separately.
 *  - Lower `particleCount` on reduced quality tiers; with reduced motion it does nothing.
 */
@Composable
fun RewardBurst(
    trigger: Int,
    modifier: Modifier = Modifier,
    colors: List<Color> = RewardBurstDefaults.colors,
    particleCount: Int = 28,
    durationMillis: Int = 800,
    reduceMotion: Boolean = rememberReduceMotion(),
) {
    val progress = rememberRewardBurstProgress(
        trigger = trigger,
        reduceMotion = reduceMotion,
        hasColors = colors.isNotEmpty(),
        durationMillis = durationMillis,
    )
    val particles = remember(trigger, particleCount, colors.size) {
        BurstParticles.create(seed = trigger, count = particleCount, colorCount = colors.size)
    }

    Canvas(modifier) {
        val t = progress.value
        if (t >= 1f || colors.isEmpty()) return@Canvas
        val cx = size.width / 2f
        val cy = size.height / 2f
        val half = min(size.width, size.height) / 2f
        val gravity = half * 0.45f * t * t
        val alpha = (1f - t).coerceIn(0f, 1f)
        val shrink = 1f - 0.5f * t

        for (i in 0 until particles.count) {
            val dist = particles.speed[i] * half * t
            val x = cx + cos(particles.angle[i]) * dist
            val y = cy + sin(particles.angle[i]) * dist + gravity
            drawCircle(
                color = colors[particles.colorIndex[i]].copy(alpha = alpha),
                radius = particles.radiusDp[i] * density * shrink,
                center = Offset(x, y),
            )
        }
    }
}


/**
 * Drives a single reward burst. Kept internal so Compose UI tests can exercise cancellation and
 * reduced-motion changes against the real animation state, not a mirrored test-only value.
 */
@Composable
internal fun rememberRewardBurstProgress(
    trigger: Int,
    reduceMotion: Boolean,
    hasColors: Boolean,
    durationMillis: Int,
): Animatable<Float, androidx.compose.animation.core.AnimationVector1D> {
    val progress = remember { Animatable(1f) } // 1f = idle/finished
    LaunchedEffect(trigger, reduceMotion, hasColors, durationMillis) {
        if (trigger <= 0 || reduceMotion || !hasColors) {
            progress.snapTo(1f)
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis.coerceAtLeast(0), easing = LinearOutSlowInEasing),
        )
    }
    return progress
}

private class BurstParticles(
    val count: Int,
    val angle: FloatArray,
    val speed: FloatArray,
    val radiusDp: FloatArray,
    val colorIndex: IntArray,
) {
    companion object {
        fun create(seed: Int, count: Int, colorCount: Int): BurstParticles {
            val rnd = Random(seed)
            val n = count.coerceAtLeast(0)
            val safeColors = colorCount.coerceAtLeast(1)
            return BurstParticles(
                count = n,
                angle = FloatArray(n) { rnd.nextFloat() * (2f * Math.PI.toFloat()) },
                speed = FloatArray(n) { 0.45f + rnd.nextFloat() * 0.55f },
                radiusDp = FloatArray(n) { 2f + rnd.nextFloat() * 3.5f },
                colorIndex = IntArray(n) { rnd.nextInt(safeColors) },
            )
        }
    }
}

/** PLACEHOLDER values. Replace with palette colors from the project. */
object RewardBurstDefaults {
    val colors = listOf(
        Color(0xFFFFC94A),
        Color(0xFF2EE6C8),
        Color(0xFFFF5468),
        Color(0xFFFFFFFF),
    )
}
