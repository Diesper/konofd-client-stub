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
    PatchMethodInfo(
      method = PatchMethod.Hook,
      title = "(deprecated) Manual hooking + memory scan",
      subtitle = "Dynamically patch libil2cpp.so to run memory scan when needed constants were just loaded. Does not cause a connection error. Does not work with libhoudini.",
      requiresCustomLoader = true
    ),
    PatchMethodInfo(
      method = PatchMethod.Scan,
      title = "(deprecated) Off-thread memory scan",
      subtitle = "Start a background thread and wait until constants are loaded. Does cause a one-time connection error.",
      requiresCustomLoader = true
    )
  )
}

