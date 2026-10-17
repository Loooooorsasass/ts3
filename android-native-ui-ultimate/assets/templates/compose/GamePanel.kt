package com.example.game.ui.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * TEMPLATE — panel with drop shadow, gradient body, edge stroke, inner highlight, optional title band.
 * Replace placeholder tokens with project tokens. Content goes in the inset area.
 */
@Immutable
data class GamePanelTokens(
    val bodyTop: Color,
    val bodyBottom: Color,
    val edge: Color,
    val shadow: Color,
    val innerStroke: Color,
    val titleBand: Color,
    val onTitle: Color,
    val cornerRadius: Dp = 22.dp,
    val edgeWidth: Dp = 2.dp,
    val shadowOffset: Dp = 4.dp,
)

@Composable
fun GamePanel(
    modifier: Modifier = Modifier,
    tokens: GamePanelTokens = GamePanelDefaults.tokens,
    title: String? = null,
    titleStyle: TextStyle = GamePanelDefaults.titleStyle,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .drawBehind {
                val r = CornerRadius(tokens.cornerRadius.toPx())
                val edge = tokens.edgeWidth.toPx()
                val shadowPx = tokens.shadowOffset.toPx()
                val bodyHeight = size.height - shadowPx

                // Drop shadow: hard offset copy of the silhouette.
                drawRoundRect(
                    color = tokens.shadow,
                    topLeft = Offset(0f, shadowPx),
                    size = Size(size.width, bodyHeight),
                    cornerRadius = r,
                )
                // Body.
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(tokens.bodyTop, tokens.bodyBottom),
                        endY = bodyHeight,
                    ),
                    size = Size(size.width, bodyHeight),
                    cornerRadius = r,
                )
                // Edge.
                drawRoundRect(
                    color = tokens.edge,
                    topLeft = Offset(edge / 2f, edge / 2f),
                    size = Size(size.width - edge, bodyHeight - edge),
                    cornerRadius = r,
                    style = Stroke(width = edge),
                )
                // Inner highlight stroke, slightly inside the edge.
                val inner = edge + 3.dp.toPx()
                drawRoundRect(
                    color = tokens.innerStroke,
                    topLeft = Offset(inner, inner),
                    size = Size(size.width - inner * 2f, bodyHeight - inner * 2f),
                    cornerRadius = CornerRadius((r.x - 3.dp.toPx()).coerceAtLeast(0f)),
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
            .padding(bottom = tokens.shadowOffset)
            .padding(tokens.edgeWidth),
    ) {
        if (title != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        tokens.titleBand,
                        RoundedCornerShape(
                            topStart = tokens.cornerRadius - tokens.edgeWidth,
                            topEnd = tokens.cornerRadius - tokens.edgeWidth,
                        ),
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = title,
                    style = titleStyle.copy(color = tokens.onTitle),
                    modifier = Modifier.semantics { heading() },
                )
            }
        }
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/** PLACEHOLDER values (Candy Cartoon example). */
object GamePanelDefaults {
    val tokens = GamePanelTokens(
        bodyTop = Color(0xFF3F4391),
        bodyBottom = Color(0xFF34377C),
        edge = Color(0xFF1B1D4A),
        shadow = Color(0x66120F33),
        innerStroke = Color(0x33FFFFFF),
        titleBand = Color(0xFF444A9A),
        onTitle = Color(0xFFFFFFFF),
    )
    val titleStyle = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
    )
}
