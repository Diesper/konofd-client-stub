package jp.assasans.konofd.stub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

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
