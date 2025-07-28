allprojects {
  repositories {
    google()
    mavenCentral()
  }
}

tasks.register<Delete>("clean").configure {
  delete(rootProject.layout.buildDirectory)
}
