#[derive(Debug)]
pub struct VersionPatches {
  /// Offset of the `il2cpp::vm::MetadataCache::LoadMetadataFile` function.
  pub load_metadata_file_offset: usize,
  /// Offset of the `il2cpp::vm::MetadataCache::GetDataDir` function.
  pub get_data_dir_offset: usize,
  pub static_url: &'static str,
  pub rsa_key_parts: &'static [&'static str],
  pub skip_logo: &'static [(usize, u32)],
}

/// # How to get version-specific offsets
///
/// ## Offsets
/// 1. Open `libil2cpp.so` in a disassembler of your choice;
/// 2. Go to Strings list and search for "global-metadata.dat"
/// 3. Go to a single cross-reference to that string
///    (e.g. `bool __fastcall sub_2A1C928(_DWORD *a1, int *a2)`)
/// 4. From there, find the function that is called with `"global-metadata.dat"` as argument
///    (e.g. `__int64 __fastcall sub_2A39514(const char *a0, ...)`). It may have a lot of other
///    parameters in declaration, but those are fake ones added by the decompiler;
/// 5. **The offset of that function is `load_metadata_file_offset`;**
/// 6. Enter that function and look for a call to a function inside it,
///    immediately after which a string `"Metadata"` is assigned to some variable;
/// 7. **The offset of that function is `get_data_dir_offset`.**
///
/// ## Static URL
/// There isn't really a single best way to get it, you can either:
/// * Dump `global-metadata.dat` with Il2CppDumper and look for a string starting
///   with "https://" and ending with ".json"
///   (e.g. `https://static.konosubafd.jp/versions/5.9.0_cewf8vjh3rscsw88.json`);
///   around it will be another strings with the same base URL - the shared part is what you need.
/// * Use MITM proxy (e.g. Charles Proxy) to intercept the app's network traffic.
///
/// ## RSA key parts
/// 1. Go to `Wonder.Util.NetWork.NetworkUtil.__c$$_.cctor_b__13_0` (or similar) function in
///    the disassembler, assuming you already imported Il2CppDumper's output into it;
/// 2. Look for all `CodeStage.AntiCheat.ObscuredTypes.ObscuredString$$op_Implicit_45043312(...)`
///    (or similar) calls inside that function;
/// 3. Write down all field names used in the order of their appearance
///    (e.g. `Wonder_Util_TextUtil_TypeInfo->static_fields->_qq_k__BackingField`);
/// 4. Go to `Wonder.Util.TextUtil$$.cctor` function and look for assignments to those fields.
///    For example:
///    ```c
///    v38 = CodeStage_AntiCheat_ObscuredTypes_ObscuredString__op_Implicit(StringLiteral_5452, 0);
///    v39 = Wonder_Util_TextUtil_TypeInfo->static_fields;
///    v39->_qq_k__BackingField = v38;
///    sub_29C4B98(&v39->_qq_k__BackingField, v38);
///    ```
/// 5. Go to each of those `StringLiteral_xxxx` variables and write down their values
///    to [rsa_key_parts] in the same order.
///
/// ## Skip logo offsets
/// 1. Search for `UnityEngine.WaitForSeconds$$.ctor` calls in `Wonder.UI.Title.TitleScene$$Init`
///    related functions (e.g. `Wonder.UI.Title.TitleScene._Init_d__65$$MoveNext`);
/// 2.
pub static VERSIONS: &[(&str, VersionPatches)] = &[
  ("4.11.5-global", VersionPatches {
    load_metadata_file_offset: 0x00000000017b6774,
    get_data_dir_offset: 0x0000000001811764,
    static_url: "https://static-prd-wonder.sesisoft.com/",
    // RSA-1024
    rsa_key_parts: &[
      "6dNRoG04n56HX2LiOA",
      "kpCC9fgjxvMKDyZGyx",
      "35Owh/sOU8HjpOdGHB",
      "y96ytzw9WMxzyvJkl2",
      "9Q82mc4y7zKy3SkchV",
      "C16mnckCO26kf6Wn4X",
      "e5lN0i7Ot5kIueWY2i",
      "oo8iRudj/EbNdumTU8",
      "I7oC7dWuvIEovK4eDJ",
      "dFJO2tzZ8=",
    ],
    skip_logo: &[
      // Sesisoft logo, MOVI V0.16B, #0
      (0x0000000004742bd8, 0x4e000000),
      // Sumzap logo, MOVI V0.16B, #0
      (0x0000000004742c88, 0x4e000000),
    ],
  }),
  ("5.9.0-jp", VersionPatches {
    load_metadata_file_offset: 0x0000000002a39514,
    get_data_dir_offset: 0x00000000029e1f78,
    static_url: "https://static.konosubafd.jp/",
    // RSA-2024, 253 bytes * 8
    // I have no idea why they decided to use such an odd key size
    rsa_key_parts: &[
      "56Q9CwCD2z9KoMSK1PLPIe+xR0Hy6Xq+1t7",
      "FZsYAyZyBWSJsonsgyhaXq3bw81excPJkJ9",
      "IIHXGRoywn3DBBvmV1uYCODBNdxtsX8Dee9",
      "L31VCq+Fe7VyNld2sDkxFtb9eblHYfDFqJU",
      "RexZmeLUoQPIYMuZ/nRzoKJ45HyXEIgDb2D",
      "q/fwKVi6nzbrDZzsLtffQdHW7Hn3PoDPA2J",
      "J2KLJRZN7gdREgrUoKzGODdSdzlYC8ghEJ6",
      "R0TWSk9eN7509+V+Y82WPj+viO7A0bpbko5",
      "97JhXOUTvGckOjnuJ7NXPxOJCYyZyvOV6Fm",
      "kNF13cUl6jxAHE0+4Xrfylw==",
    ],
    skip_logo: &[
      // Sumzap logo
      // LDR S0, [X8,#dword_1279D00@PAGEOFF] -> FMOV S0, WZR
      // (0x00000000031af434, 0x1e270000)
    ],
  }),
];
