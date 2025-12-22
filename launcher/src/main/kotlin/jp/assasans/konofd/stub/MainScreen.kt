package jp.assasans.konofd.stub

import android.app.Application
import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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

          val gameVersion = BuildConfig.VERSION_NAME.substringBefore("/")
          val launcherVersion = BuildConfig.VERSION_NAME.substringAfter("/")

          Text(
            text = "Game version: $gameVersion, launcher version: $launcherVersion",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
          )
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

        val gameVersion = BuildConfig.VERSION_NAME.substringBefore("/")
        val launcherVersion = BuildConfig.VERSION_NAME.substringAfter("/")

        Text(
          text = "Game version: $gameVersion, launcher version: $launcherVersion",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )
      }
    }
  }

  if(state.dialogVisible && state.serverVerification != null) {
    PublicKeyDialog(
      initialTimeSeconds = state.autoLaunchTimeSeconds,
      serverVerification = state.serverVerification!!,
      patchMethod = state.method,
      onCancel = { viewModel.cancelServerCheck() },
      onOk = { viewModel.closeServerCheckDialog() },
      onLaunch = { viewModel.launchGame() }
    )
  }
}
