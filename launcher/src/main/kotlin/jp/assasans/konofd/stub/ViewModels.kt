package jp.assasans.konofd.stub

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URI
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val PREFS_NAME = "launcher_settings"
private const val PREF_METHOD = "method"
private const val PREF_SKIP_LOGO = "skip_logo"
private const val PREF_AUTO_LAUNCH = "auto_launch"
private const val PREF_SERVER_URL = "server_url"
private const val PREF_SERVER_URL_HISTORY = "server_url_history"

private const val DEFAULT_SKIP_LOGO = true
private const val DEFAULT_AUTO_LAUNCH = false
private const val DEFAULT_SERVER_URL = "https://axel.assasans.dev/static/"

enum class PatchMethod {
  None,
  Hook,
  Scan,
  LoadMetadataFileHook,
}

private fun PatchMethod.prefValue(): String = when(this) {
  PatchMethod.None -> "none"
  PatchMethod.Hook -> "hook"
  PatchMethod.Scan -> "scan"
  PatchMethod.LoadMetadataFileHook -> "loadMetadataFileHook"
}

private fun methodFromPref(value: String?): PatchMethod = when(value) {
  "none" -> PatchMethod.None
  "hook" -> PatchMethod.Hook
  "scan" -> PatchMethod.Scan
  "loadMetadataFileHook" -> PatchMethod.LoadMetadataFileHook
  else -> {
    Log.w("LauncherViewModel", "Unknown patch method in prefs: $value")
    PatchMethod.None
  }
}

data class LauncherState(
  val method: PatchMethod = PatchMethod.Hook,
  val isSkipLogoEnabled: Boolean = DEFAULT_SKIP_LOGO,
  val isAutoLaunchEnabled: Boolean = DEFAULT_AUTO_LAUNCH,

  val serverVerification: ServerVerification? = null,
  val dialogVisible: Boolean = false,
  val autoLaunchTimeSeconds: Int? = null, // Only stores the initial value

  val serverUrlHistory: List<String> = emptyList(),
)

sealed class ServerVerification {
  data object Loading : ServerVerification()

  data class Success(
    val url: String,
    val pem: String,
    val modulusBase64: String
  ) : ServerVerification()

  data object SuccessNoServer : ServerVerification()

  data class Failure(val exception: Throwable) : ServerVerification()
}

class LauncherViewModel(
  context: Context,
  val launcher: IGameLauncher
) : ViewModel() {
  private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
    PREFS_NAME,
    Context.MODE_PRIVATE
  )

  private val _state = MutableStateFlow(LauncherState())
  val state: StateFlow<LauncherState> = _state.asStateFlow()

  val serverUrlState = TextFieldState(initialText = DEFAULT_SERVER_URL)
  val authTokenState = TextFieldState()

  val isCustomLoader: Boolean = isRunningCustomLoader(context) ?: false // true for preview

  private var serverCheckJob: Job? = null

  init {
    try {
      loadInitialData()
    } catch(exception: Exception) {
      exception.printStackTrace()
    }

    if(!isCustomLoader) {
      _state.value = _state.value.copy(
        method = PatchMethod.None,
        isSkipLogoEnabled = false
      )
    }

    if(_state.value.isAutoLaunchEnabled) {
      println("Auto-launch enabled, checking server...")
      checkServer(isAutoLaunch = true)
    }
  }

  private fun loadInitialData() {
    val method = methodFromPref(sharedPrefs.getString(PREF_METHOD, "hook"))
    val skipLogo = sharedPrefs.getBoolean(PREF_SKIP_LOGO, DEFAULT_SKIP_LOGO)
    val autoLaunch = sharedPrefs.getBoolean(PREF_AUTO_LAUNCH, DEFAULT_AUTO_LAUNCH)
    val serverUrl =
      sharedPrefs.getString(PREF_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL

    val serverUrlHistory =
      sharedPrefs.getString(PREF_SERVER_URL_HISTORY, "[]") ?: "[]"
    val serverUrlHistoryArray = JSONArray(serverUrlHistory)

    _state.value = _state.value.copy(
      method = method,
      isSkipLogoEnabled = skipLogo,
      isAutoLaunchEnabled = autoLaunch,
      serverUrlHistory = List(serverUrlHistoryArray.length()) { index ->
        serverUrlHistoryArray.getString(index)
      }
    )

    println("Loaded server URL: $serverUrl")
    serverUrlState.setTextAndPlaceCursorAtEnd(serverUrl)
  }

  fun setLaunchMethod(method: PatchMethod) {
    _state.value = _state.value.copy(method = method)
    sharedPrefs.edit { putString(PREF_METHOD, method.prefValue()) }
    if(method == PatchMethod.None) {
      _state.value = _state.value.copy(
        serverVerification = ServerVerification.SuccessNoServer
      )
    }
  }

  fun setSkipLogoEnabled(value: Boolean) {
    _state.value = _state.value.copy(isSkipLogoEnabled = value)
    sharedPrefs.edit { putBoolean(PREF_SKIP_LOGO, value) }
  }

  fun setAutoLaunchEnabled(value: Boolean) {
    _state.value = _state.value.copy(isAutoLaunchEnabled = value)
    sharedPrefs.edit { putBoolean(PREF_AUTO_LAUNCH, value) }
  }

  fun setServerUrl(url: String) {
    serverUrlState.setTextAndPlaceCursorAtEnd(url)
    saveServerUrl()
  }

  fun saveServerUrl() {
    val url = serverUrlState.text.toString().trim()
    if(!isValidServerUrl(url)) return

    sharedPrefs.edit {
      putString(PREF_SERVER_URL, url)
    }
    addServerUrlToHistory(url)
  }

  fun checkServer(isAutoLaunch: Boolean = false) {
    if(_state.value.method == PatchMethod.None) {
      _state.value = _state.value.copy(
        serverVerification = ServerVerification.SuccessNoServer,
        autoLaunchTimeSeconds = if(isAutoLaunch) 3 else 10
      )
      setDialogVisible(true)
      return
    }

    if(!isValidServerUrl(serverUrlState.text.toString().trim())) {
      _state.value = _state.value.copy(
        serverVerification = ServerVerification.Failure(
          IllegalArgumentException("Invalid server URL")
        )
      )
      return
    }

    serverCheckJob?.cancel()
    serverCheckJob = viewModelScope.launch {
      _state.value = _state.value.copy(
        serverVerification = ServerVerification.Loading
      )
      setDialogVisible(true)

      val result = verifyServer(serverUrlState.text.toString().trim())
      _state.value = _state.value.copy(
        serverVerification = result,
        autoLaunchTimeSeconds = if(result is ServerVerification.Success) {
          if(isAutoLaunch) 3 else 10
        } else null
      )

      if(result is ServerVerification.Success) {
        addServerUrlToHistory(result.url)
      }
    }
  }

  fun addServerUrlToHistory(url: String) {
    if(!isValidServerUrl(url)) return

    val currentState = _state.value
    val newHistory = currentState.serverUrlHistory.toMutableList()
    newHistory.remove(url) // Remove existing entry if present
    newHistory.add(0, url)
    _state.value = currentState.copy(serverUrlHistory = newHistory)

    sharedPrefs.edit {
      val historyJson = JSONArray(newHistory).toString()
      putString(PREF_SERVER_URL_HISTORY, historyJson)
    }
  }

  fun cancelServerCheck() {
    serverCheckJob?.cancel()
    serverCheckJob = null
    _state.value = _state.value.copy(
      serverVerification = null,
      autoLaunchTimeSeconds = null
    )
    setDialogVisible(false)
  }

  fun closeServerCheckDialog() {
    setDialogVisible(false)
  }

  fun isDirectUrlValid(): Boolean = isValidServerUrl(serverUrlState.text.toString().trim())

  fun canLaunch(): Boolean {
    val currentState = _state.value
    return when {
      currentState.method == PatchMethod.None -> true
      else -> currentState.serverVerification is ServerVerification.Success
    }
  }

  fun launchGame() {
    val currentState = _state.value

    val verification = currentState.serverVerification
    when(verification) {
      is ServerVerification.Success -> {
        val serverUrl = serverUrlState.text.toString().trim()

        launcher.launchGame(
          serverUrl,
          verification.modulusBase64,
          currentState.method,
          currentState.isSkipLogoEnabled,
          authTokenState.text.toString()
        )
      }

      is ServerVerification.SuccessNoServer -> {
        launcher.launchGame("", "", PatchMethod.None, false, "")
      }

      else -> {
        throw IllegalStateException(
          "Cannot launch game: server verification is not successful"
        )
      }
    }
  }

  fun setDialogVisible(visible: Boolean) {
    _state.value = _state.value.copy(dialogVisible = visible)
    if(!visible) {
      _state.value = _state.value.copy(autoLaunchTimeSeconds = null)
      println("Dialog closed, resetting time to launch")
    }
  }

  class Factory(
    private val context: Context,
    private val launcher: IGameLauncher,
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if(modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
        return LauncherViewModel(context, launcher) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}

fun fingerprintFromPemV2(pem: String): List<List<String>>? {
  return runCatching {
    val der = decodePemToDer(pem)
    val digest = MessageDigest.getInstance("SHA-256").digest(der)
    digest.joinToString("") { "%02X".format(it) }
      .chunked(4)
      .chunked(4)
  }.getOrNull()
}

fun decodePemToDer(pem: String): ByteArray {
  val base64 = pem
    .replace("-----BEGIN PUBLIC KEY-----", "")
    .replace("-----END PUBLIC KEY-----", "")
    .replace("\\s".toRegex(), "")
  return Base64.decode(base64, Base64.DEFAULT)
}

fun extractModulusFromPem(der: ByteArray): String {
  val publicKey = KeyFactory.getInstance("RSA")
    .generatePublic(X509EncodedKeySpec(der)) as RSAPublicKey
  return Base64.encodeToString(publicKey.modulus.toByteArray(), Base64.NO_WRAP)
}

fun isValidServerUrl(url: String): Boolean {
  if(url.isBlank()) return false
  return try {
    val uri = URI(url.trim())
    val scheme = uri.scheme?.lowercase()
    scheme == "https" && !uri.host.isNullOrBlank()
  } catch(_: Exception) {
    false
  }
}

suspend fun verifyServer(baseUrl: String): ServerVerification = withContext(Dispatchers.IO) {
  val base = baseUrl.trim().trimEnd('/')
  try {
    val publicKeyUrl = "$base/public.pem"
    val pem = makeHttpRequest(publicKeyUrl)

    // Parse modulus + fingerprint
    val modulus = extractModulusFromPem(decodePemToDer(pem))

    ServerVerification.Success(
      url = base,
      pem = pem,
      modulusBase64 = modulus,
    )
  } catch(exception: Exception) {
    exception.printStackTrace()
    ServerVerification.Failure(exception)
  }
}

suspend fun makeHttpRequest(urlString: String): String {
  val client = HttpClient(OkHttp)
  return try {
    client.get(urlString).bodyAsText()
  } finally {
    client.close()
  }
}

@SuppressLint("UnsafeDynamicallyLoadedCode")
fun isRunningCustomLoader(context: Context): Boolean? {
  val path = context.applicationInfo.nativeLibraryDir ?: return null
  val fullPath = "$path/libmain.so"

  try {
    try {
      System.load(fullPath)
    } catch(exception: UnsatisfiedLinkError) {
      Log.e(
        "LauncherViewModel",
        "Failed to load native library from path: $fullPath - $exception. Trying to load from default library name."
      )
      System.loadLibrary("main")
    }
  } catch(exception: UnsatisfiedLinkError) {
    Log.e(
      "LauncherViewModel",
      "Failed to load native library: $fullPath - $exception"
    )
    return null
  } catch(exception: SecurityException) {
    Log.e(
      "LauncherViewModel",
      "Failed to load native library: $fullPath - $exception"
    )
    return null
  }

  try {
    if(NativeLoaderSupplemental.supplemental_verify()) {
      return true
    } else {
      throw RuntimeException("NativeLoaderSupplemental.verify failed")
    }
  } catch(exception: UnsatisfiedLinkError) {
    Log.e("LauncherViewModel", "Failed to verify native loader: " + exception.message)
    return false
  }
}
