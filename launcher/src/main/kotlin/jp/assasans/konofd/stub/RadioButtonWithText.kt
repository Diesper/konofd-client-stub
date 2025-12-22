package jp.assasans.konofd.stub

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RadioButtonWithText(
  selected: Boolean,
  enabled: Boolean = true,
  onClick: () -> Unit,
  title: String,
  subtitle: String?,
  isCompact: Boolean = false
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .alpha(if(enabled) 1f else 0.38f)
      .clickable(
        enabled = enabled,
        onClick = onClick,
        indication = ripple(),
        interactionSource = remember { MutableInteractionSource() }
      )
      .padding(vertical = if(isCompact) 8.dp else 12.dp)
  ) {
    RadioButton(
      selected = selected,
      enabled = enabled,
      onClick = null // Use Row's clickable instead
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontSize = if(isCompact) 13.sp else 14.sp,
      )
      if(subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
          fontSize = if(isCompact) 11.sp else 12.sp,
          maxLines = if(isCompact) 2 else Int.MAX_VALUE,
          overflow = if(isCompact) TextOverflow.Ellipsis else TextOverflow.Clip
        )
      }
    }
  }
}
