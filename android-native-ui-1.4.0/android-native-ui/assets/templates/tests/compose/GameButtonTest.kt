package com.example.game.ui.template

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/*
 * TEMPLATE — Compose UI test for GameButton. Place in app/src/androidTest/... (instrumented).
 * Requires androidx.compose.ui:ui-test-junit4 and ui-test-manifest in debugImplementation.
 * Uses reduceMotion = true and hapticType = null so the test is deterministic and silent.
 * Not compiled or run yet: run it in the target project before trusting it.
 */
class GameButtonTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun enabledButtonIsClickableAndInvokesCallbackOnce() {
        var clicks = 0
        rule.setContent {
            GameButton(text = "Play", onClick = { clicks++ }, reduceMotion = true, hapticType = null)
        }
        rule.onNodeWithText("Play")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun disabledButtonIsNotEnabledAndDoesNotInvokeCallback() {
        var clicks = 0
        rule.setContent {
            GameButton(
                text = "Locked",
                onClick = { clicks++ },
                enabled = false,
                reduceMotion = true,
                hapticType = null,
            )
        }
        // Assert the observable state and the behavior, not the presence of a semantics action:
        // clickable(enabled = false) may still list an OnClick action next to the disabled flag.
        rule.onNodeWithText("Locked").assertIsNotEnabled()
        rule.onNodeWithText("Locked").performClick()
        rule.runOnIdle { assertEquals(0, clicks) }
    }

    @Test
    fun buttonHasButtonRole() {
        rule.setContent {
            GameButton(text = "Play", onClick = {}, reduceMotion = true, hapticType = null)
        }
        rule.onNodeWithText("Play").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button),
        )
    }

    @Test
    fun vietnameseLabelWithDiacriticsIsDisplayed() {
        rule.setContent {
            GameButton(text = "Tiếp tục", onClick = {}, reduceMotion = true, hapticType = null)
        }
        rule.onNodeWithText("Tiếp tục").assertIsDisplayed()
    }
}
