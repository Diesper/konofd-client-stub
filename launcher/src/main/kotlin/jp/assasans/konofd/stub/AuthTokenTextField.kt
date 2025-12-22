package jp.assasans.konofd.stub

import android.graphics.Typeface
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTokenTextField(
  state: LauncherState,
  viewModel: LauncherViewModel,
) {
  var passwordHidden by rememberSaveable { mutableStateOf(true) }

  OutlinedSecureTextField(
    state = viewModel.authTokenState,
    label = { Text("Authentication token") },
    enabled = state.serverVerification !is ServerVerification.Loading,
    modifier = Modifier.fillMaxWidth(),
    textObfuscationMode =
      if(passwordHidden) TextObfuscationMode.RevealLastTyped
      else TextObfuscationMode.Visible,
    trailingIcon = {
      val description = if(passwordHidden) "Show token" else "Hide token"
      TooltipBox(
        positionProvider =
          TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text(description) } },
        state = rememberTooltipState(),
      ) {
        IconButton(onClick = { passwordHidden = !passwordHidden }) {
          val visibilityIcon = if(passwordHidden) {
            Icons.Default.Visibility
          } else {
            Icons.Default.VisibilityOff
          }
          Icon(imageVector = visibilityIcon, contentDescription = description)
        }
      }
    },
  )

  // Text(
  //   text = "This token is used along with local UUID to associate your device.",
  //   style = MaterialTheme.typography.bodySmall,
  //   modifier = Modifier
  //     .fillMaxWidth()
  //     .padding(top = 8.dp),
  //   color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
  // )

  var showTokenInfoDialog by remember { mutableStateOf(false) }

  // Text that opens a dialog on click
  Text(
    text = "How does it work?",
    style = MaterialTheme.typography.bodySmall,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 4.dp)
      .clickable { showTokenInfoDialog = true }
      .padding(4.dp),
    color = MaterialTheme.colorScheme.primary,
    textAlign = TextAlign.End,
  )

  if(showTokenInfoDialog) {
    val scrollState = rememberScrollState()

    Dialog(
      onDismissRequest = { showTokenInfoDialog = false },
      properties = DialogProperties(
        usePlatformDefaultWidth = false
      ),
    ) {
      Card(
        modifier = Modifier
          .wrapContentWidth()
          .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
      ) {
        Column(
          modifier = Modifier
            .padding(16.dp)
            .widthIn(min = 280.dp, max = 560.dp)
            .verticalScroll(scrollState)
            .verticalScrollBar(scrollState),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
            text = "Game authentication",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            style = MaterialTheme.typography.bodySmall,
            text = buildAnnotatedString {
              append("The server authenticates you using a ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("uuid")
              }
              append(" field sent by the client. ")

              append("It consists of a local UUID and an advertising ID. ")

              append("There is also an ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("advertising_id")
              }
              append(" field that consists of just the advertising ID. ")

              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("uuid")
              }
              append(" is cached unless you log out from the game, or server logs you out using ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("ERROR_SAME_USER")
              }
              appendLine(".")
              appendLine()

              withStyle(
                style = SpanStyle(
                  fontStyle = FontStyle.Italic
                )
              ) {
                append("A local UUID")
              }
              appendLine(" is a pair of two GUIDs locally generated by the client on the first login.")
              appendLine()

              append("Axel reuses the advertising ID as an ")
              withStyle(
                style = SpanStyle(
                  fontStyle = FontStyle.Italic
                )
              ) {
                append("authentication token")
              }
              append(". It is returned by the Java API, so stub allows you to set it to any string instead.")
            })

          HorizontalDivider(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
          )

          Text(
            style = MaterialTheme.typography.bodySmall,
            text = buildAnnotatedString {
              append("Axel allows you to play on one account from multiple devices. ")
              append("If you log in to a new device with a token that you have used before, the server will automatically link the local UUID to your account. ")
              append("If there is no account associated with the authentication token, the server will create a new one. ")
              appendLine()
              appendLine()

              append("If you do not specify an authentication token or use an official APK while creating an account, it will not be possible to play your account on multiple devices.")
              appendLine()
              appendLine()

              append("Once you change your authentication token, you will need to log out in the game so that ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("uuid")
              }
              append(" is regenerated with the new token. Axel will automatically log you out if the authentication token differs from the one stored in ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("uuid")
              }
              append(".")
            })

          HorizontalDivider(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 16.dp),
          )

          Text(
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            text = "TL;DR",
            modifier = Modifier.padding(bottom = 8.dp)
          )

          Text(
            style = MaterialTheme.typography.bodySmall,
            text = buildAnnotatedString {
              append("You may think of the authentication token as a username-and-password pair for your account, and use RFC 7617 ")
              withStyle(
                style = SpanStyle(
                  fontFamily = FontFamily(Typeface.MONOSPACE),
                  background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
              ) {
                append("username:P@$\$w0rd")
              }
              append(" format if you want. ")
              withStyle(
                style = SpanStyle(
                  fontWeight = FontWeight.Bold,
                )
              ) {
                // append("Keep the token secret as anyone who has it can log in to your account.")
                append("ANYONE WHO HAS YOUR AUTHENTICATION TOKEN CAN LOG IN TO YOUR ACCOUNT!")
              }
            }
          )

          Spacer(modifier = Modifier.height(16.dp))
          // I'm gonna type a whole bunch of random stuff right now so it's gonna make it seem like I have lots to talk about regarding this topic. I'll uncover the spoiler tags for certain words such as Aqua and people will be confused as to why she was mentioned. I can also do funny things such as mentioning among us which makes people even more confused with what's going on. Before I give you the answer, I do want to give a shutout to my homie, Harambe After the death of Harambe, I feel like the world really turned upside down. All things that could've gone wrong, went wrong. Some people said some cruel things to him such as " they deserved to die ", but I don't think that's very nice. Are you feeling hungry while reading this? Would you like a slice of pizza ? If you do, I'll be more than happy to treat you to a slice if you could tell me more about this one Wikipedia article titled: List of selfie-related injuries and deaths.

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(
              onClick = { showTokenInfoDialog = false }
            ) {
              Text("OK")
            }
          }
        }
      }
    }
  }
}
