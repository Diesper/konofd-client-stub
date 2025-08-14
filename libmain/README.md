# KonoFD `libmain.so` reimplementation

Warning: lots of questionable and bad unsafe code inside!

## Build instructions

You need to have [Android NDK](https://developer.android.com/ndk) installed.

```sh
rustup target add aarch64-linux-android
cargo install cargo-ndk
cargo ndk -t arm64-v8a build --release -o "../unityLibrary/src/main/jniLibs/"
```

![](https://safebooru.org/images/1559/43eaacb9a1d080193781b572a8893aca48215769.jpg)
