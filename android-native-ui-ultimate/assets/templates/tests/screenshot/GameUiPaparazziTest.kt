package com.example.game.ui.template

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import com.example.game.ui.template.views.SegmentedBarView
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

/**
 * TEMPLATE Paparazzi golden tests. Add the Paparazzi Gradle plugin using the project's compatible
 * pinned release, then copy this file to src/test. These tests are examples; record and review the
 * baselines in the target project before treating them as verification evidence.
 */
class GameUiPaparazziTest {
    @get:Rule val paparazzi = Paparazzi()

    @Test
    fun gameButtonEnabled() {
        paparazzi.snapshot(name = "game-button-enabled") {
            GameButton(
                text = "Tiếp tục",
                onClick = {},
                modifier = Modifier.width(240.dp),
                reduceMotion = true,
            )
        }
    }

    @Test
    fun gameButtonPressed() {
        paparazzi.snapshot(name = "game-button-pressed") {
            GameButton(
                text = "Tiếp tục",
                onClick = {},
                modifier = Modifier.width(240.dp),
                reduceMotion = true,
                interactionSource = AlwaysPressedInteractionSource(),
            )
        }
    }

    @Test
    fun gameButtonDisabled() {
        paparazzi.snapshot(name = "game-button-disabled") {
            GameButton(
                text = "Tiếp tục",
                onClick = {},
                modifier = Modifier.width(240.dp),
                enabled = false,
                reduceMotion = true,
            )
        }
    }

    @Test
    fun healthBarNormal() {
        paparazzi.snapshot(name = "health-bar-normal") {
            HealthBarExample(
                fraction = 0.8f,
                healthLabel = "Máu 80%",
                normalStateDescription = "Máu bình thường",
                lowStateDescription = "Máu thấp",
                lowWarningLabel = "Cảnh báo máu thấp",
                modifier = Modifier.width(280.dp),
                reduceMotion = true,
            )
        }
    }

    @Test
    fun healthBarLow() {
        paparazzi.snapshot(name = "health-bar-low") {
            HealthBarExample(
                fraction = 0.2f,
                healthLabel = "Máu 20%",
                normalStateDescription = "Máu bình thường",
                lowStateDescription = "Máu thấp",
                lowWarningLabel = "Cảnh báo máu thấp",
                modifier = Modifier.width(280.dp),
                reduceMotion = true,
            )
        }
    }

    @Test
    fun viewsHealthBarLow() {
        val bar = SegmentedBarView(paparazzi.context).apply {
            contentDescription = "Máu 20%"
            setStateDescriptions("Máu bình thường", "Máu thấp")
            setFraction(0.2f)
        }
        paparazzi.snapshot(view = bar, name = "views-health-bar-low")
    }

    @Test
    fun gamePanelAndLongVietnameseText() {
        paparazzi.snapshot(name = "game-panel-vietnamese-long-text") {
            GamePanel(
                modifier = Modifier.width(320.dp),
                title = "Phần thưởng nhiệm vụ",
            ) {
                BasicText("Bạn đã hoàn thành nhiệm vụ và nhận được phần thưởng đặc biệt!")
            }
        }
    }
}

/** A deterministic interaction source for capturing the pressed visual state without touch input. */
private class AlwaysPressedInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = flowOf(PressInteraction.Press(Offset.Zero))
    override suspend fun emit(interaction: Interaction) = Unit
    override fun tryEmit(interaction: Interaction): Boolean = true
}
