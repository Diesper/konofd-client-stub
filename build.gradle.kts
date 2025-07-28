plugins {
  id("com.android.application") version "8.11.1" apply false
  id("com.android.library") version "8.11.1" apply false
  id("org.jetbrains.kotlin.android") version "2.2.0" apply false
}

allprojects {
  repositories {
    google()
    mavenCentral()
  }
}

tasks.register<Delete>("clean").configure {
  delete(rootProject.layout.buildDirectory)
}
