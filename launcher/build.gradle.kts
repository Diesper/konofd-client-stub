plugins {
  id("com.android.application") version "8.11.1"
}

dependencies {
  implementation(project(":unityLibrary"))
}

android {
  namespace = "jp.assasans.konofd.stub"
  compileSdk = 36

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdk = 22
    targetSdk = 36
    applicationId = "jp.assasans.konofd.stub"
    ndk {
      // noinspection ChromeOsAbiSupport - game does not support x86_64
      abiFilters += "arm64-v8a"
    }
    versionCode = 1
    versionName = "1.0"
  }

  lint {
    abortOnError = false
  }

  androidResources {
    noCompress += listOf(
      ".unity3d",
      ".ress",
      ".resource",
      ".obb",
      ".bundle",
      ".unityexp"
    )
    ignoreAssetsPattern = "!.svn:!.git:!.ds_store:!*.scc:.*:!CVS:!thumbs.db:!picasa.ini:!*~"
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt")))
      signingConfig = signingConfigs.getByName("debug")
    }
    release {
      isMinifyEnabled = false
      setProguardFiles(listOf(getDefaultProguardFile("proguard-android.txt")))
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  packaging {
    jniLibs.keepDebugSymbols += "*/arm64-v8a/*.so"
  }

  bundle {
    language {
      enableSplit = false
    }

    density {
      enableSplit = false
    }

    abi {
      enableSplit = true
    }
  }
}
