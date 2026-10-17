package com.example.game.ui.template

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/*
 * TEMPLATE — Compose UI tests for SegmentedBar. Place in app/src/androidTest/... (instrumented).
 * Not compiled or run yet: run them in the target project before trusting them.
 *
 * The first test checks the accessibility semantics. Those report the TARGET value only, so they
 * cannot prove that the animation converges. The other tests use the internal `rememberBarAnimation`
 * seam from SegmentedBar.kt and assert on the real `front` (fill) and `ghost` (trail) values while
 * driving the test clock by hand. `internal` is visible to androidTest of the same module; if the
 * template is moved into a separate module, make the seam public or move these tests with it.
 */
class SegmentedBarTest {

    @get:Rule
    val rule = createComposeRule()

    private companion object {
        const val DELTA = 0.001f
    }

    @Test
    fun exposesLabelAndTargetProgress() {
        rule.setContent {
            SegmentedBar(fraction = 0.5f, label = "Health 50%", reduceMotion = true)
        }
        rule.onNodeWithContentDescription("Health 50%")
            .assertIsDisplayed()
            .assert(
                SemanticsMatcher("progress is 0.5") {
                    it.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo)?.current == 0.5f
                },
            )
    }

    @Test
    fun exposesLocalizedStateDescriptionForLowHealth() {
        rule.setContent {
            SegmentedBar(
                fraction = 0.2f,
                label = "Health",
                stateDescription = "Low health",
                reduceMotion = true,
            )
        }
        rule.onNodeWithContentDescription("Health")
            .assert(SemanticsMatcher("low-health state is exposed") {
                it.config.getOrNull(SemanticsProperties.StateDescription) == "Low health"
            })
    }

    @Test
    fun rapidChangesConvergeToLastTarget() {
        var value by mutableStateOf(1f)
        lateinit var animation: BarAnimation
        rule.mainClock.autoAdvance = false
        rule.setContent { animation = rememberBarAnimation(value, reduceMotion = false) }

        rule.runOnIdle { value = 0.2f }          // damage
        rule.mainClock.advanceTimeBy(100)        // interrupt it while it is still moving
        rule.runOnIdle { value = 0.7f }          // heal: this is the LAST target
        rule.mainClock.advanceTimeBy(2_000)      // longer than every animation and delay involved

        rule.runOnIdle {
            assertEquals("fill must end on the last target", 0.7f, animation.front.value, DELTA)
            assertEquals("trail must end on the last target", 0.7f, animation.ghost.value, DELTA)
        }
    }

    @Test
    fun damageMovesFillFirstThenTrailCatchesUp() {
        var value by mutableStateOf(1f)
        lateinit var animation: BarAnimation
        rule.mainClock.autoAdvance = false
        rule.setContent { animation = rememberBarAnimation(value, reduceMotion = false) }

        rule.runOnIdle { value = 0.4f }
        // The fill tween is 120 ms; the trail only starts after a 350 ms delay.
        rule.mainClock.advanceTimeBy(200)
        rule.runOnIdle {
            assertEquals("fill should already be at the target", 0.4f, animation.front.value, DELTA)
            assertTrue(
                "trail should still be above the fill, was ${animation.ghost.value}",
                animation.ghost.value > 0.9f,
            )
        }

        rule.mainClock.advanceTimeBy(1_000)
        rule.runOnIdle {
            assertEquals("trail should have caught up", 0.4f, animation.ghost.value, DELTA)
        }
    }

    @Test
    fun reducedMotionJumpsStraightToTarget() {
        var value by mutableStateOf(1f)
        lateinit var animation: BarAnimation
        rule.mainClock.autoAdvance = false
        rule.setContent { animation = rememberBarAnimation(value, reduceMotion = true) }

        rule.runOnIdle { value = 0.3f }
        rule.mainClock.advanceTimeBy(50)

        rule.runOnIdle {
            assertEquals(0.3f, animation.front.value, DELTA)
            assertEquals(0.3f, animation.ghost.value, DELTA)
        }
    }
}
