package jp.assasans.konofd.stub

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
@Preview
fun PublicKeyDialogPreview() {
  PublicKeyDialog(
    serverVerification = ServerVerification.Success(
      url = "https://axel.assasans.dev/static",
      pem = """
        -----BEGIN PUBLIC KEY-----
        MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPtV42y5gtf/9zH+gzEQzEqC+j
        j/jC6wHdBtXdkvDrUAgKU1KO8w1sgUGlnEkn0CKfIZd7oLWWaoKF+uWEG04PBm6C
        op4VoAxE31QaUNHJKSElmQzJ659TyQEES8eTjjQoy9hOAJdvBgkqOpfIz0o6Beej
        w7qDc4SwtcjAYx6+DQIDAQAB
        -----END PUBLIC KEY-----
      """.trimIndent(),
      modulusBase64 = ""
    ),
    patchMethod = PatchMethod.LoadMetadataFileHook,
    initialTimeSeconds = 10,
    onCancel = {},
    onOk = {},
    onLaunch = {},
  )
}

@Composable
fun PublicKeyDialog(
  serverVerification: ServerVerification,
  patchMethod: PatchMethod,
  initialTimeSeconds: Int?,
  onCancel: () -> Unit,
  onOk: (() -> Unit),
  onLaunch: (() -> Unit),
) {
  var remainingSeconds by remember { mutableIntStateOf(initialTimeSeconds ?: 0) }
  var progress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(initialTimeSeconds) {
    if(initialTimeSeconds != null && initialTimeSeconds > 0) {
      val totalMillis = initialTimeSeconds * 1000L
      val startTime = withFrameMillis { it }

      while(true) {
        val currentTime = withFrameMillis { it }
        val elapsed = currentTime - startTime
        val remaining = (totalMillis - elapsed).coerceAtLeast(0)

        remainingSeconds = ((remaining + 999) / 1000).toInt() // Round up
        progress = (remaining.toFloat() / totalMillis)

        if(remaining <= 0) {
          onLaunch()
          break
        }
      }
    }
  }

  Dialog(onDismissRequest = {
    when(serverVerification) {
      is ServerVerification.Loading -> onCancel()
      is ServerVerification.Failure -> onOk()
      is ServerVerification.Success -> onOk()
      is ServerVerification.SuccessNoServer -> onOk()
    }
  }) {
    Card(
      modifier = Modifier
        .wrapContentWidth()
        .wrapContentHeight(),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column(
        modifier = Modifier
          .padding(16.dp)
          .widthIn(min = 280.dp, max = 560.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = when(serverVerification) {
            is ServerVerification.Loading -> "Loading server configuration…"
            is ServerVerification.Failure -> "Failed to load server configuration"
            is ServerVerification.Success -> "Server configuration loaded successfully"
            is ServerVerification.SuccessNoServer -> "Starting game without custom server"
          },
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        when(serverVerification) {
          is ServerVerification.Loading -> {
            CircularProgressIndicator(
              modifier = Modifier.size(48.dp)
            )
          }

          is ServerVerification.Failure -> {
            Text(
              text = serverVerification.exception.toString(),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error,
              textAlign = TextAlign.Center
            )
          }

          is ServerVerification.Success -> {
            Text(
              text = "Server URL",
              style = MaterialTheme.typography.titleSmall,
              textAlign = TextAlign.Center
            )
            Text(
              text = serverVerification.url,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontFamily = FontFamily(Typeface.MONOSPACE),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Server public key fingerprint",
              style = MaterialTheme.typography.titleSmall,
              textAlign = TextAlign.Center
            )
            Text(
              text = requireNotNull(fingerprintFromPemV2(serverVerification.pem))
                .map { group -> group.joinToString(" ") }
                .joinToString("\n"),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontFamily = FontFamily(Typeface.MONOSPACE),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Patching method",
              style = MaterialTheme.typography.titleSmall,
              textAlign = TextAlign.Center
            )
            Text(
              text = PatchMethodRegistry.availableMethods.single { it.method == patchMethod }.title,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }

          is ServerVerification.SuccessNoServer -> {
            Text(
              text = "No custom server is set.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          }
        }

        if(initialTimeSeconds != null && initialTimeSeconds > 0 &&
          (serverVerification is ServerVerification.Success || serverVerification is ServerVerification.SuccessNoServer)
        ) {
          Spacer(modifier = Modifier.height(16.dp))
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          when(serverVerification) {
            is ServerVerification.Loading -> {
              TextButton(
                onClick = onCancel,
                modifier = Modifier.padding(end = 8.dp)
              ) {
                Text("Cancel")
              }
            }

            is ServerVerification.Failure -> {
              TextButton(onClick = { onOk() }) { Text("OK") }
            }

            is ServerVerification.Success, is ServerVerification.SuccessNoServer -> {
              TextButton(onClick = { onOk() }) { Text("Cancel") }
              Spacer(modifier = Modifier.width(8.dp))
              Button(onClick = { onLaunch() }) {
                if(initialTimeSeconds != null && remainingSeconds > 0) {
                  Text("Launch Game ($remainingSeconds)")
                } else {
                  Text("Launch Game")
                }
              }
            }
          }
        }
      }
    }
  }
}
