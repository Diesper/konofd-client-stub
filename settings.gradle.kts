pluginManagement {
  repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
  }

  buildscript {
    repositories {
      mavenCentral()
      google()
      gradlePluginPortal()
    }

    dependencies {
      classpath("com.android.tools:r8:8.11.18")
    }
  }
}

include(":launcher", ":unityLibrary")
