package jp.assasans.konofd.stub

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalScrollBar(
  state: ScrollState,
  color: Color = Color.White,
  ratio: Float = 3f,
  width: Dp = 1.dp
): Modifier {
  val targetAlpha = if(state.isScrollInProgress) 0.6f else 0.15f
  val duration = if(state.isScrollInProgress) 150 else 500

  val alpha by animateFloatAsState(
    targetValue = targetAlpha,
    animationSpec = tween(durationMillis = duration)
  )

  return drawWithContent {
    drawContent()

    val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f
    val barHeight = (this.size.height / ratio)
    val barRange = (this.size.height - barHeight) / state.maxValue
    if(needDrawScrollbar) {
      val position = state.value * barRange
      drawRect(
        color = color.copy(alpha = alpha),
        topLeft = Offset(this.size.width + 6.dp.toPx() - width.toPx(), position),
        size = Size(width.toPx(), barHeight)
      )
    }
  }
}
