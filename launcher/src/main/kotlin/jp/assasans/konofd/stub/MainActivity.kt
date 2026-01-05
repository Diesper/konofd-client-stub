package jp.assasans.konofd.stub

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.unity3d.player.UnityPlayerActivity
import jp.assasans.konofd.stub.ui.theme.ExportedTheme
import java.net.URLEncoder

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
        // REMOVED: 1 (manual hook)
        // REMOVED: 2 (off-thread scan)
        PatchMethod.LoadMetadataFileHook -> 3
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
