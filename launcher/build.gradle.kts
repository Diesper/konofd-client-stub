import java.io.FileInputStream
import java.util.Properties

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  kotlin("android")
}

dependencies {
  implementation(project(":unityLibrary"))

  implementation("io.ktor:ktor-client-core:3.2.3")
  implementation("io.ktor:ktor-client-okhttp:3.2.3")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

  implementation("androidx.core:core-ktx:1.16.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.10.0")
  implementation("androidx.activity:activity:1.8.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
  implementation("androidx.navigation:navigation-compose:2.9.3")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")

  val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
  implementation(composeBom)

  // Material Design 3
  implementation("androidx.compose.material3:material3:1.5.0-alpha01")
  implementation("androidx.compose.material:material-icons-core")
  implementation("androidx.compose.material:material-icons-extended")
  implementation("androidx.activity:activity-compose:1.10.1")

  // Android Studio Preview support
  implementation("androidx.compose.ui:ui-tooling-preview")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")
}

android {
  namespace = "jp.assasans.konofd.stub"
  compileSdk = 36

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = "11"
  }

  buildFeatures {
    viewBinding = true
    compose = true
    buildConfig = true
  }

  defaultConfig {
    minSdk = 22
    targetSdk = 36
    applicationId = "jp.assasans.konofd.stub"
    ndk {
      // noinspection ChromeOsAbiSupport - game does not support x86_64
      abiFilters += "arm64-v8a"
    }
    versionCode = 370
    versionName = "4.11.5/stub-1.1.0"
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

  signingConfigs {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

    create("main") {
      keyAlias = keystoreProperties["keyAlias"] as String
      keyPassword = keystoreProperties["keyPassword"] as String
      storeFile = file(keystoreProperties["storeFile"] as String)
      storePassword = keystoreProperties["storePassword"] as String
    }
  }

  buildTypes {
    debug {
      isMinifyEnabled = false
      setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt")))
      signingConfig = signingConfigs.getByName("main")
    }
    release {
      isMinifyEnabled = false
      setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt")))
      signingConfig = signingConfigs.getByName("main")
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
