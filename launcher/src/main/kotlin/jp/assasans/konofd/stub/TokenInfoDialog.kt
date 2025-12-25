package jp.assasans.konofd.stub

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

@Composable
@Preview
fun TokenInfoDialogPreview() {
  TokenInfoDialog(
    token = "a1b2c3d4e5f67890abcdef123456789001g1aabba1b2c3d4e5f67890abcdef123456789",
    userId = "123456789",
    hasConfirmed = true,
    onConfirm = {},
    onDismiss = {}
  )
}

@Composable
fun TokenInfoDialog(
  token: String,
  userId: String,
  hasConfirmed: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  val timeStepMs = 60 * 1000L

  var currentTotp by remember { mutableStateOf(generateTotp(token)) }
  var progress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(token, hasConfirmed) {
    if(!hasConfirmed) return@LaunchedEffect

    while(true) {
      val now = System.currentTimeMillis()
      currentTotp = generateTotp(token, now)

      val remainingMs = getTotpRemainingMillis(now)
      progress = remainingMs.toFloat() / timeStepMs

      // Update every 100ms for smooth progress bar
      delay(100)
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .wrapContentWidth()
        .wrapContentHeight(),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .widthIn(min = 280.dp, max = 400.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "In-game account",
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if(!hasConfirmed) {
          Text(
            text = "READ THIS IMPORTANT INFORMATION",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = buildString {
              append("You are about to view your in-game account authentication information. ")
              append("Anyone with access to it can log into your account.")
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(24.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onConfirm) {
              Text("Continue")
            }

            TextButton(onClick = onDismiss) {
              Text("Close")
            }
          }
        } else {
          Text(
            text = "User ID",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
          Text(
            text = userId,
            style = MaterialTheme.typography.headlineSmall.copy(
              fontFamily = FontFamily(Typeface.MONOSPACE),
              fontSize = 32.sp,
              letterSpacing = 2.sp
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "User token",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
          )
          Text(
            text = token,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Typeface.MONOSPACE),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "One-time password",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
          )
          Text(
            text = "HMAC-SHA256, 60s interval",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = formatTotpGrouped(currentTotp),
            style = MaterialTheme.typography.headlineLarge.copy(
              fontFamily = FontFamily(Typeface.MONOSPACE),
              fontSize = 32.sp,
              letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(12.dp))

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
          )

          Text(
            text = "Refreshes in ${(progress * 60).toInt()}s",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
          )

          Text(
            text = "Do not share this token or TOTP with anyone.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
          )

          Spacer(modifier = Modifier.height(24.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = onDismiss) {
              Text("Close")
            }
          }
        }
      }
    }
  }
}
