package jp.assasans.konofd.stub

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.unity3d.player.UnityPlayerActivity
import jp.assasans.konofd.stub.ui.theme.ExportedTheme
import java.net.URLEncoder
import kotlin.time.Duration

class MainActivity : ComponentActivity() {
  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ExportedTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
          MainScreen(
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
          )
        }
      }
    }
  }
}

interface IGameLauncher {
  fun launchGame(
    serverUrl: String,
    publicKey: String,
    patchMethod: PatchMethod,
    skipLogo: Boolean,
    authToken: String,
  )
}

class AndroidGameLauncher(
  private val application: Application
) : IGameLauncher {
  override fun launchGame(
    serverUrl: String,
    publicKey: String,
    patchMethod: PatchMethod,
    skipLogo: Boolean,
    authToken: String
  ) {
    AdvertisingIdClient.advertisingId = URLEncoder.encode(authToken, "UTF-8")

    val intent = Intent(application, UnityPlayerActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    intent.putExtra("server_url", serverUrl)
    intent.putExtra("public_key", publicKey)
    intent.putExtra(
      "method",
      when(patchMethod) {
        PatchMethod.None -> 0
        PatchMethod.Hook -> 1
        PatchMethod.Scan -> 2
      }
    )
    intent.putExtra("skip_logo", skipLogo)
    application.startActivity(intent)
  }
}

class StubGameLauncher : IGameLauncher {
  override fun launchGame(
    serverUrl: String,
    publicKey: String,
    patchMethod: PatchMethod,
    skipLogo: Boolean,
    authToken: String
  ) {
    println("Launching game with server URL: $serverUrl, public key: $publicKey, method: $patchMethod, skip logo: $skipLogo, auth token: $authToken")
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@Preview(
  showBackground = true,
  widthDp = 800, heightDp = 350
)
fun MainScreen(
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(0.dp)
) {
  val context = LocalContext.current
  val configuration = LocalConfiguration.current

  val launcher = if(context.applicationContext is Application) {
    AndroidGameLauncher(context.applicationContext as Application)
  } else {
    StubGameLauncher()
  }
  val viewModel: LauncherViewModel =
    viewModel(factory = LauncherViewModel.Factory(context, launcher))

  val state by viewModel.state.collectAsState()

  val isServerSectionVisible = state.method != PatchMethod.None
  val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
  val ownContentPadding = if(isLandscape) 16.dp else 16.dp

  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(contentPadding)
  ) {
    if(isLandscape) {
      // Landscape layout: split into two columns
      Row(
        modifier = modifier
          .fillMaxSize()
          .padding(ownContentPadding)
          .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Left column: Method selection
        Column(
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "Patching method",
            style = MaterialTheme.typography.bodyLarge
          )

          Spacer(modifier = Modifier.height(8.dp))

          PatchingMethodSelection(
            selectedMethod = state.method,
            isSkipLogoEnabled = state.isSkipLogoEnabled,
            isAutoLaunchEnabled = state.isAutoLaunchEnabled,
            onMethodChange = { viewModel.setLaunchMethod(it) },
            onToggleSkipLogo = { viewModel.setSkipLogoEnabled(!state.isSkipLogoEnabled) },
            onToggleAutoLaunch = { viewModel.setAutoLaunchEnabled(!state.isAutoLaunchEnabled) },
            isCompact = true,
            viewModel = viewModel
          )
        }

        // Right column: Server configuration and launch
        Column(
          modifier = Modifier.weight(1f)
        ) {
          if(isServerSectionVisible) {
            ServerConfigurationSection(
              state = state,
              viewModel = viewModel
            )
          } else {
            Text(
              text = "Server configuration is disabled when patching method is set to None.",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.fillMaxWidth()
            )
          }

          // AuthTokenTextField(
          //   state = state,
          //   viewModel = viewModel
          // )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { viewModel.launchGame() },
            enabled = viewModel.canLaunch(),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp)
          ) {
            Text("Launch Game")
          }

          if(!viewModel.isCustomLoader) {
            Text(
              text = "Custom libmain.so is not available. Patching is not available.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.error,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
            )
          }
        }
      }
    } else {
      // Portrait layout: single column
      Column(
        modifier = modifier
          .fillMaxSize()
          .padding(ownContentPadding)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Patching method",
          style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        PatchingMethodSelection(
          selectedMethod = state.method,
          isSkipLogoEnabled = state.isSkipLogoEnabled,
          isAutoLaunchEnabled = state.isAutoLaunchEnabled,
          onMethodChange = { viewModel.setLaunchMethod(it) },
          onToggleSkipLogo = { viewModel.setSkipLogoEnabled(!state.isSkipLogoEnabled) },
          onToggleAutoLaunch = { viewModel.setAutoLaunchEnabled(!state.isAutoLaunchEnabled) },
          isCompact = false,
          viewModel = viewModel
        )

        HorizontalDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        )

        if(isServerSectionVisible) {
          ServerConfigurationSection(
            state = state,
            viewModel = viewModel
          )
        } else {
          Text(
            text = "Server configuration is disabled when patching method is set to None.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth()
          )
        }

        // AuthTokenTextField(
        //   state = state,
        //   viewModel = viewModel
        // )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = { viewModel.launchGame() },
          enabled = viewModel.canLaunch(),
          modifier = Modifier.fillMaxWidth(),
          contentPadding = PaddingValues(vertical = 24.dp)
        ) {
          Text("Launch Game")
        }

        if(!viewModel.isCustomLoader) {
          Text(
            text = "Custom libmain.so is not available. Patching is not available.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          )
        }
      }
    }
  }

  if(state.dialogVisible && state.serverVerification != null) {
    val timeToLaunch by viewModel.dialogTimeToLaunch.collectAsState()
    PublicKeyDialog(
      timeToLaunch = timeToLaunch,
      serverVerification = state.serverVerification!!,
      onCancel = { viewModel.cancelServerCheck() },
      onOk = { viewModel.closeServerCheckDialog() },
      onLaunch = { viewModel.launchGame() }
    )
  }
}

@Composable
private fun PatchingMethodSelection(
  selectedMethod: PatchMethod,
  isSkipLogoEnabled: Boolean,
  isAutoLaunchEnabled: Boolean,
  onMethodChange: (PatchMethod) -> Unit,
  onToggleSkipLogo: () -> Unit,
  onToggleAutoLaunch: () -> Unit = {},
  isCompact: Boolean,
  viewModel: LauncherViewModel,
) {
  Column {
    RadioButtonWithText(
      selected = selectedMethod == PatchMethod.None,
      onClick = { onMethodChange(PatchMethod.None) },
      title = "None",
      subtitle = "Run the game as-is without any patching.",
      isCompact = isCompact
    )
    RadioButtonWithText(
      selected = selectedMethod == PatchMethod.Hook,
      onClick = { onMethodChange(PatchMethod.Hook) },
      title = "Hooking",
      subtitle = "Dynamically patch libil2cpp.so to run patching when constants were just loaded. Does not cause a connection error. Does not work with libhoudini.",
      isCompact = isCompact,
      enabled = viewModel.isCustomLoader
    )
    RadioButtonWithText(
      selected = selectedMethod == PatchMethod.Scan,
      onClick = { onMethodChange(PatchMethod.Scan) },
      title = "Off-thread scan",
      subtitle = "Start a background thread and wait until constants are loaded. Does cause a one-time connection error.",
      isCompact = isCompact,
      enabled = viewModel.isCustomLoader
    )

    Spacer(modifier = Modifier.height(8.dp))

    CheckBoxWithText(
      selected = isSkipLogoEnabled,
      onClick = onToggleSkipLogo,
      title = "Skip startup logo",
      subtitle = "Skip Sesisoft and Sumzap logos to speed up launch.",
      isCompact = isCompact,
      enabled = viewModel.isCustomLoader
    )

    CheckBoxWithText(
      selected = isAutoLaunchEnabled,
      onClick = onToggleAutoLaunch,
      title = "Auto-launch game",
      subtitle = "Automatically launch the game skipping the launcher screen after a short delay.",
      isCompact = isCompact
    )
  }
}

@Composable
private fun ServerConfigurationSection(
  state: LauncherState,
  viewModel: LauncherViewModel
) {
  ServerTextField(
    state = state,
    viewModel = viewModel
  )

//
//   // Show a compact card with selected server info
//   selectedServer?.let { server ->
//     Spacer(modifier = Modifier.height(12.dp))
//     OutlinedCard(modifier = Modifier.fillMaxWidth()) {
//       Column(Modifier.padding(12.dp)) {
//         Text(
//           server.displayName,
//           style = MaterialTheme.typography.titleSmall,
//           fontWeight = FontWeight.SemiBold
//         )
//         Spacer(Modifier.height(4.dp))
//         Text(
//           server.url,
//           style = MaterialTheme.typography.bodySmall,
//           maxLines = 1,
//           overflow = TextOverflow.Ellipsis
//         )
//         val fp = viewModel.fingerprintFromPem(server.pem)
//         if(fp != null) {
//           Spacer(Modifier.height(4.dp))
//           Text(
//             fp,
//             style = MaterialTheme.typography.bodySmall,
//             color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
//           )
//         }
//       }
//     }
//   }
}

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

@Composable
fun ServerTextField(
  state: LauncherState,
  viewModel: LauncherViewModel,
) {
  var expanded by remember { mutableStateOf(false) }

  Column {
    OutlinedTextField(
      state = viewModel.serverUrlState,
      label = { Text("Server URL") },
      enabled = state.serverVerification !is ServerVerification.Loading,
      isError = !viewModel.isDirectUrlValid(),
      supportingText = {
        if(!viewModel.isDirectUrlValid()) {
          Text("Enter a valid HTTPS URL, e.g. https://axel.assasans.dev/static")
        }
      },
      modifier = Modifier.fillMaxWidth(),
      keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
      trailingIcon = {
        IconButton(onClick = { expanded = !expanded }) {
          Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Show history"
          )
        }
      },
    )

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      state.serverUrlHistory.forEach { item ->
        DropdownMenuItem(
          onClick = {
            viewModel.setServerUrl(item)
            expanded = false
          },
          text = { Text(item) }
        )
      }
    }

    Button(
      enabled = viewModel.isDirectUrlValid(),
      modifier = Modifier.fillMaxWidth(),
      onClick = { viewModel.checkServer() }
    ) {
      Text("Check")
    }
  }
}

@Composable
fun PublicKeyDialog(
  serverVerification: ServerVerification,
  timeToLaunch: Duration?,
  onCancel: () -> Unit,
  onOk: (() -> Unit),
  onLaunch: (() -> Unit),
) {
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
            is ServerVerification.Loading -> "Loading server public key…"
            is ServerVerification.Failure -> "Failed to load server public key"
            is ServerVerification.Success -> "Server public key loaded successfully"
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
              TextButton(onClick = { onOk() }) { Text("OK") }
              Spacer(modifier = Modifier.width(8.dp))
              Button(onClick = { onLaunch() }) {
                if(timeToLaunch != null) {
                  Text("Launch Game (${timeToLaunch.inWholeSeconds})")
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

@Composable
fun CheckBoxWithText(
  selected: Boolean,
  enabled: Boolean = true,
  onClick: () -> Unit,
  title: String,
  subtitle: String? = null,
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
    Checkbox(
      checked = selected,
      enabled = enabled,
      onCheckedChange = null // Use Row's clickable instead
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
