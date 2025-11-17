import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

val keyStoreProperties = Properties()
val keyStoreFile = rootProject.file("key.properties")
if (keyStoreFile.exists()) {
    keyStoreProperties.load(FileInputStream(keyStoreFile))
}

android {
    namespace = "com.kdongsu5509.iamhere"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = "27.0.12077973"

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }

    signingConfigs {
        create("release") {
            if (keyStoreFile.exists()) {
                storeFile = file(keyStoreProperties["storeFile"] as String)
                storePassword = keyStoreProperties["storePassword"] as String
                keyAlias = keyStoreProperties["keyAlias"] as String
                keyPassword = keyStoreProperties["keyPassword"] as String
            }
        }
    }

    defaultConfig {
        applicationId = "com.kdongsu5509.iamhere"
        minSdk = 23
        targetSdk = 36
        compileSdk = 36

        versionCode = flutter.versionCode
        versionName = flutter.versionName

        // kakao key
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())
        val kakaoKey = localProperties.getProperty("kakao.native.app.key")

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKey
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName("release")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

flutter {
    source = "../.."
}
