# KonoFD `libmain.so` reimplementation

Warning: lots of questionable and bad unsafe code inside!

<img src="https://safebooru.org/images/1559/43eaacb9a1d080193781b572a8893aca48215769.jpg" height="400" />

## Build instructions

You need to have [Android NDK](https://developer.android.com/ndk) installed.

```sh
rustup target add aarch64-linux-android
cargo install cargo-ndk
cargo ndk -t arm64-v8a build --release -o "../unityLibrary/src/main/jniLibs/"
```

## Supported versions

* 4.11.5, global
* 5.9.0, JP

Tutorial for adding support for other versions is available in [versions.rs](src/versions.rs).
