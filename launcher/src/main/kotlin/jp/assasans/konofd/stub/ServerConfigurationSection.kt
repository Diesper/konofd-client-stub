package jp.assasans.konofd.stub

import androidx.compose.runtime.Composable

@Composable
fun ServerConfigurationSection(
  state: LauncherState,
  viewModel: LauncherViewModel
) {
  ServerTextField(
    state = state,
    viewModel = viewModel
  )

  // // Show a compact card with selected server info
  // selectedServer?.let { server ->
  //   Spacer(modifier = Modifier.height(12.dp))
  //   OutlinedCard(modifier = Modifier.fillMaxWidth()) {
  //     Column(Modifier.padding(12.dp)) {
  //       Text(
  //         server.displayName,
  //         style = MaterialTheme.typography.titleSmall,
  //         fontWeight = FontWeight.SemiBold
  //       )
  //       Spacer(Modifier.height(4.dp))
  //       Text(
  //         server.url,
  //         style = MaterialTheme.typography.bodySmall,
  //         maxLines = 1,
  //         overflow = TextOverflow.Ellipsis
  //       )
  //       val fp = viewModel.fingerprintFromPem(server.pem)
  //       if(fp != null) {
  //         Spacer(Modifier.height(4.dp))
  //         Text(
  //           fp,
  //           style = MaterialTheme.typography.bodySmall,
  //           color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
  //         )
  //       }
  //     }
  //   }
  // }
}
