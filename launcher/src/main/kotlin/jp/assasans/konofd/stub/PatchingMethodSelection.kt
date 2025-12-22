package jp.assasans.konofd.stub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PatchingMethodSelection(
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
    PatchMethodRegistry.availableMethods.forEach { methodInfo ->
      RadioButtonWithText(
        selected = selectedMethod == methodInfo.method,
        onClick = { onMethodChange(methodInfo.method) },
        title = methodInfo.title,
        subtitle = methodInfo.subtitle,
        isCompact = isCompact,
        enabled = !methodInfo.requiresCustomLoader || viewModel.isCustomLoader
      )
    }

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
