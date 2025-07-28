# Setup

Note: This is not a offline version of the game, nor it has an integrated server.
This is a version without LIAPP protection, allowing you to connect to non-official servers without root permissions.

Download the official APK from [APKPure](https://apkpure.com/konosuba-fantastic-days/com.nexon.konosuba/versions) or [my server](https://smb.assasans.dev/konofd/apk/).
If you have any versions that are not listed on my server, I'd be grateful if you could share them.

I do not recommend using version 4.11.6 as it freezes the game after server errors. Use 4.11.5 instead.

The original files are located at the following locations:

* Native libraries: `config.arm64_v8a.apk!/lib/arm64-v8a/`
* Assets: `com.nexon.konosuba.apk!/assets/`
* Encrypted `global-metadata.dat`:
  `com.nexon.konosuba.apk!/assets/bin/Data/Managed/Metadata/global-metadata.dat`

You have to dump the decrypted `global-metadata.dat` (follow part 1
of [this guide](https://github.com/Assasans/axel/blob/main/DUMPING.md)).
Alternatively, you can [download](https://smb.assasans.dev/konofd/global-metadata/) the decrypted
ones.

```sh
# Assets directory can have invalid timestamps after APK extraction,
# we need to fix them so that Gradle doesn't complain
find '<assets dir>' -exec touch {} +

./gradlew prepareNativeLibraries -Pkonofd.src.natives='<natives dir>'
./gradlew prepareAssets -Pkonofd.src.assets='<assets dir>'
./gradlew prepareGlobalMetadata -Pkonofd.src.global_metadata='<decrypted global metadata file>'

# konofd.patch.url is the server URL that reimplements "https://static-prd-wonder.sesisoft.com/".
# konofd.patch.pubkey is the public RSA-1024 key of the server.
./gradlew patchGlobalMetadata \
  -Pkonofd.patch.url='https://axel.assasans.dev/static/' \
  -Pkonofd.patch.pubkey="$HOME/dev/rust/axel/pubkey.pem"

# Install the app using:
./gradlew installDebug

# Or build the APK and install it manually:
./gradlew build
adb install-multiple launcher-debug.apk
```

Some actions may cause the game to freeze, meaning an unhandled Java exception was thrown and some API needs to be implemented.

Tested on:

* Waydroid 1.5.4 with Android 11 and libhoudini
* Xiaomi Redmi Note 11 with Android 12
