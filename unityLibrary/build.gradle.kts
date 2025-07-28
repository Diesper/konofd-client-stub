import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
  id("com.android.library")
  kotlin("android")
}

dependencies {
  implementation(files("libs/unity-classes.jar"))
  implementation(files("libs/com.onevcat.uniwebview.jar"))

  // noinspection GradleDependency - original APK uses this version
  implementation("com.google.android.gms:play-services-tasks:18.1.0")

  implementation("androidx.core:core-ktx:1.16.0")
}

android {
  namespace = "com.unity3d.player"
  compileSdk = 36

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  // kotlinOptions {
  //   jvmTarget = "1.8"
  // }

  defaultConfig {
    minSdk = 22
    ndk {
      // noinspection ChromeOsAbiSupport - game does not support x86_64
      abiFilters += "arm64-v8a"
    }
    consumerProguardFiles("proguard-unity.txt")
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
    ignoreAssetsPattern += "!.svn:!.git:!.ds_store:!*.scc:.*:!CVS:!thumbs.db:!picasa.ini:!*~"
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
}

tasks {
  register("prepareNativeLibraries") {
    group = "build setup"

    doLast {
      val srcNatives = project.findProperty("konofd.src.natives")?.toString()
      if(srcNatives.isNullOrEmpty()) {
        throw GradleException("Property 'konofd.src.natives' is not set.")
      }
      if(!file(srcNatives).exists()) {
        throw GradleException("Source natives directory '$srcNatives' does not exist.")
      }

      val jniLibsDir = file(layout.projectDirectory.dir("original/jni_libs_original/arm64-v8a"))
      jniLibsDir.mkdirs()

      copy {
        from(fileTree(srcNatives) {
          include(
            /* Unity */
            "libunity.so",
            "libil2cpp.so",
            "libmain.so",

            /* Dependencies */
            "libcri_ware_unity.so",
            "libLive2DCubismCore.so",

            /* Crashes without this one, but we can exclude other Firebase and Google trash libraries */
            "libFirebaseCppApp-10_6_0.so",
          )
        })
        into(jniLibsDir)
        eachFile {
          logger.lifecycle("Copying native library: $sourcePath")
        }
      }
    }
  }

  named { it == "mergeDebugJniLibFolders" || it == "mergeReleaseJniLibFolders" }.configureEach {
    val jniLibsSrc = layout.projectDirectory.dir("src/main/jniLibs").asFile
    val jniLibsOriginal = layout.projectDirectory.dir("original/jni_libs_original").asFile
    if(!jniLibsOriginal.exists()) {
      throw GradleException("Original jniLibs directory '$jniLibsOriginal' does not exist. Please run 'prepareNativeLibraries' task first.")
    }

    val jniLibsMerged = layout.buildDirectory.get().asFile
      .resolve("intermediates/merged_jni_libs")
      .resolve(if(name.contains("Debug")) "debug/mergeDebugJniLibFolders" else "release/mergeReleaseJniLibFolders")
      .resolve("out/lib")
    jniLibsMerged.mkdirs()

    inputs.dir(jniLibsSrc)
    inputs.dir(jniLibsOriginal)
    outputs.dir(jniLibsMerged)

    doLast {
      sync {
        from(jniLibsOriginal)
        into(jniLibsMerged)
        include("**/*.so")
      }

      // Libraries in src/jniLibs/ take precedence over the original APK libraries
      copy {
        from(jniLibsSrc)
        into(jniLibsMerged)
        include("**/*.so")
      }

      val expectedLibraries = listOf(
        "libunity.so",
        "libil2cpp.so",
        "libmain.so",
        "libcri_ware_unity.so",
        "libLive2DCubismCore.so",
        "libFirebaseCppApp-10_6_0.so"
      )

      expectedLibraries.forEach { lib ->
        if(!file(jniLibsMerged.resolve("arm64-v8a/$lib")).exists()) {
          logger.error("Native library $lib is missing in the merged jniLibs directory.")
        }
      }
      logger.lifecycle("Merged native libraries are ready in: $jniLibsMerged")
    }
  }

  register("prepareAssets") {
    group = "build setup"

    doLast {
      val srcAssets = project.findProperty("konofd.src.assets")?.toString()
      if(srcAssets.isNullOrEmpty()) {
        throw GradleException("Property 'konofd.src.assets' is not set.")
      }
      if(!file(srcAssets).exists()) {
        throw GradleException("Source assets directory '$srcAssets' does not exist.")
      }

      val assetsDir = layout.projectDirectory.dir("original/assets_original").asFile
      assetsDir.mkdirs()

      sync {
        from(srcAssets)
        into(assetsDir)
        exclude(
          // LIAPP trash
          "LIAPP.ini",

          // Encrypted one, we prepare it later
          "bin/Data/Managed/Metadata/global-metadata.dat",
        )
        exclude {
          // LIAPP packer
          it.file.name.startsWith(".") && it.file.name.endsWith(".dex")
        }
        eachFile {
          logger.lifecycle("Copying asset: $sourcePath")
        }
      }

      delete("LIAPP.ini", "bin/Data/Managed/Metadata/global-metadata.dat")
    }
  }

  register("prepareGlobalMetadata") {
    group = "build setup"

    doLast {
      val srcGlobalMetadata = project.findProperty("konofd.src.global_metadata")?.toString()
      if(srcGlobalMetadata.isNullOrEmpty()) {
        throw GradleException("Property 'konofd.src.global_metadata' is not set.")
      }
      if(!file(srcGlobalMetadata).exists()) {
        throw GradleException("Source global-metadata.dat file '$srcGlobalMetadata' does not exist.")
      }

      val assetsDir = layout.projectDirectory
        .dir("original/assets_global_metadata/bin/Data/Managed/Metadata")
        .asFile
      assetsDir.mkdirs()

      copy {
        from(file(srcGlobalMetadata))
        into(assetsDir)
        rename { "global-metadata.dat" }
        eachFile {
          logger.lifecycle("Copying global-metadata.dat: $sourcePath")
        }
      }
    }
  }

  named { it == "mergeDebugAssets" || it == "mergeReleaseAssets" }.configureEach {
    val assetsSrc = layout.projectDirectory.dir("src/main/assets")
    val assetsOriginal = layout.projectDirectory.dir("original/assets_original").asFile
    val assetsGlobalMetadata = layout.projectDirectory.dir("original/assets_global_metadata").asFile
    if(!assetsOriginal.exists()) {
      throw GradleException("Original assets directory '$assetsOriginal' does not exist. Please run 'prepareAssets' task first.")
    }
    if(!assetsGlobalMetadata.exists()) {
      throw GradleException("Global metadata assets directory '$assetsGlobalMetadata' does not exist. Please run 'prepareGlobalMetadata' task first.")
    }

    val assetsMerged = layout.buildDirectory.get().asFile
      .resolve("intermediates/assets")
      .resolve(if(name.contains("Debug")) "debug/mergeDebugAssets" else "release/mergeReleaseAssets")
    assetsMerged.mkdirs()

    inputs.dir(assetsSrc)
    inputs.dir(assetsOriginal)
    inputs.dir(assetsGlobalMetadata)
    outputs.dir(assetsMerged)

    doLast {
      if(!assetsMerged.exists()) {
        throw GradleException("Merged assets directory '$assetsMerged' does not exist.")
      }

      sync {
        from(assetsOriginal)
        into(assetsMerged)
        eachFile {
          logger.lifecycle("Copying original asset: $sourcePath")
        }
      }

      copy {
        from(assetsGlobalMetadata)
        into(assetsMerged)
        eachFile {
          logger.lifecycle("Copying global-metadata.dat: $sourcePath")
        }
      }

      copy {
        from(assetsSrc)
        into(assetsMerged)
        exclude("README.md")
        eachFile {
          logger.lifecycle("Copying source asset from src: $sourcePath")
        }
      }

      logger.lifecycle("Merged assets are ready in: $assetsMerged")
    }
  }

  @OptIn(ExperimentalEncodingApi::class)
  register("patchGlobalMetadata") {
    doLast {
      var inputFile = layout.projectDirectory
        .file("original/assets_global_metadata/bin/Data/Managed/Metadata/global-metadata.dat")
        .asFile
      if(!inputFile.exists()) {
        throw GradleException("Input global-metadata.dat file does not exist: ${inputFile.absolutePath}")
      }

      // Backup handling
      val backupFile = inputFile.resolveSibling("global-metadata.dat.bak")
      if(!backupFile.exists()) {
        inputFile.copyTo(backupFile, overwrite = true)
        logger.lifecycle("Backup created: ${backupFile.absolutePath}")
      } else {
        logger.lifecycle("Using existing backup: ${backupFile.absolutePath}")
      }
      inputFile = backupFile

      val outputFile = layout.projectDirectory
        .file("original/assets_global_metadata/bin/Data/Managed/Metadata/global-metadata.dat")
        .asFile

      // Get patch URL
      val patchUrl = project.findProperty("konofd.patch.url")?.toString()
      if(patchUrl.isNullOrEmpty()) {
        throw GradleException("Property 'konofd.patch.url' is not set.")
      }

      val originalUrl = "https://static-prd-wonder.sesisoft.com/"
      if(patchUrl.length > originalUrl.length) {
        throw GradleException("Patch URL '$patchUrl' is longer than the original URL '$originalUrl'.")
      }
      val paddedUrl = patchUrl + "/".repeat(originalUrl.length - patchUrl.length)
      logger.lifecycle("Using server URL: $paddedUrl")

      // Get public key
      val patchPubkey = project.findProperty("konofd.patch.pubkey")?.toString()
      if(patchPubkey.isNullOrEmpty()) {
        throw GradleException("Property 'konofd.patch.pubkey' is not set.")
      }

      val pubkeyFile = file(patchPubkey)
      if(!pubkeyFile.exists()) {
        throw GradleException("Public key file does not exist: $patchPubkey")
      }

      val pemContent = pubkeyFile.readText()
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "")
        .replace("\r", "")

      val keyBytes = Base64.Default.decode(pemContent)
      val keySpec = X509EncodedKeySpec(keyBytes)
      val keyFactory = KeyFactory.getInstance("RSA")
      val publicKey = keyFactory.generatePublic(keySpec) as RSAPublicKey

      val modulus = publicKey.modulus
      val modulusBytes = modulus.toByteArray()
      val cleanModulusBytes = if(modulusBytes[0] == 0.toByte()) {
        modulusBytes.drop(1).toByteArray()
      } else {
        modulusBytes
      }
      val modulusBase64 = Base64.Default.encode(cleanModulusBytes)
      logger.lifecycle("Using public key: $modulusBase64")

      val originalParts = listOf(
        "6dNRoG04n56HX2LiOA",
        "kpCC9fgjxvMKDyZGyx",
        "35Owh/sOU8HjpOdGHB",
        "y96ytzw9WMxzyvJkl2",
        "9Q82mc4y7zKy3SkchV",
        "C16mnckCO26kf6Wn4X",
        "e5lN0i7Ot5kIueWY2i",
        "oo8iRudj/EbNdumTU8",
        "I7oC7dWuvIEovK4eDJ",
        "dFJO2tzZ8="
      )

      require(originalParts.sumOf(String::length) == modulusBase64.length) {
        "Modulus length mismatch: expected total ${originalParts.sumOf(String::length)}, got ${modulusBase64.length}"
      }

      var offset = 0
      val modulusParts = originalParts.map { part ->
        val slice = modulusBase64.substring(offset, offset + part.length)
        offset += part.length
        slice
      }

      val replacements = mapOf(
        originalUrl to paddedUrl
      ) + originalParts.zip(modulusParts).toMap()

      // Validate replacement lengths
      replacements.forEach { (original, replacement) ->
        if(original.length != replacement.length) {
          throw GradleException("Length mismatch for replacement: '$original' (${original.length}) -> '$replacement' (${replacement.length})")
        }
      }

      val fileBytes = inputFile.readBytes()

      // Perform replacements
      replacements.forEach { (original, replacement) ->
        val originalBytes = original.toByteArray(Charsets.UTF_8)
        val replacementBytes = replacement.toByteArray(Charsets.UTF_8)

        var searchIndex = 0
        var replacementCount = 0
        while(searchIndex <= fileBytes.size - originalBytes.size) {
          val match =
            originalBytes.indices.all { fileBytes[searchIndex + it] == originalBytes[it] }
          if(match) {
            for(i in replacementBytes.indices) {
              fileBytes[searchIndex + i] = replacementBytes[i]
            }
            replacementCount++
            searchIndex += originalBytes.size
          } else {
            searchIndex++
          }
        }
        println("Replaced '$original' -> '$replacement' ($replacementCount occurrences)")
      }

      outputFile.writeBytes(fileBytes)
      println("Binary file processing completed. Output written to: ${outputFile.absolutePath}")
    }
  }
}

android {
  sourceSets {
    getByName("main") {
      jniLibs.setSrcDirs(listOf(layout.buildDirectory.dir("intermediates/jni_libs_merged")))
    }
  }
}
