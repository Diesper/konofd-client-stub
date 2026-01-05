package jp.assasans.konofd.stub

data class PatchMethodInfo(
  val method: PatchMethod,
  val title: String,
  val subtitle: String,
  val requiresCustomLoader: Boolean = false
)

object PatchMethodRegistry {
  val availableMethods = listOf(
    PatchMethodInfo(
      method = PatchMethod.None,
      title = "None",
      subtitle = "Run the game as-is without any patching.",
      requiresCustomLoader = false
    ),
    PatchMethodInfo(
      method = PatchMethod.LoadMetadataFileHook,
      title = "LoadMetadataFile (And64InlineHook)",
      subtitle = "Hook il2cpp::vm::MetadataLoader::LoadMetadataFile using And64InlineHook to return dynamically patched global-metadata.dat.",
      requiresCustomLoader = true
    ),
  )
}
