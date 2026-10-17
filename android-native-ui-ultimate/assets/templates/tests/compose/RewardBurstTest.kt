package com.example.game.ui.template

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** TEMPLATE Compose UI tests. Move into src/androidTest and run in the target project. */
class RewardBurstTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun enablingReducedMotionFinishesAnInFlightBurst() {
        var trigger by mutableStateOf(1)
        var reduceMotion by mutableStateOf(false)
        lateinit var progress: androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>
        rule.mainClock.autoAdvance = false
        rule.setContent {
            progress = rememberRewardBurstProgress(trigger, reduceMotion, hasColors = true, durationMillis = 800)
        }

        rule.mainClock.advanceTimeBy(120)
        rule.runOnIdle { assertTrue("burst should have started", progress.value < 1f) }
        rule.runOnIdle { reduceMotion = true }
        rule.mainClock.advanceTimeBy(1)
        rule.runOnIdle { assertEquals(1f, progress.value, 0.001f) }
    }

    @Test
    fun clearingTriggerResetsBurstToIdle() {
        var trigger by mutableStateOf(1)
        lateinit var progress: androidx.compose.animation.core.Animatable<Float, androidx.compose.animation.core.AnimationVector1D>
        rule.mainClock.autoAdvance = false
        rule.setContent {
            progress = rememberRewardBurstProgress(trigger, reduceMotion = false, hasColors = true, durationMillis = 800)
        }

        rule.mainClock.advanceTimeBy(100)
        rule.runOnIdle { trigger = 0 }
        rule.mainClock.advanceTimeBy(1)
        rule.runOnIdle { assertEquals(1f, progress.value, 0.001f) }
    }
}
