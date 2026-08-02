import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.stickerpack.maker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.stickerdrop.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["stickerContentProviderAuthority"] = "${applicationId}.stickercontentprovider"
    }

    signingConfigs {
        create("release") {
            val keyStoreFilePath = keystoreProperties.getProperty("KEYSTORE_FILE") ?: System.getenv("KEYSTORE_FILE")
            val keyStorePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD") ?: System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = keystoreProperties.getProperty("KEY_ALIAS") ?: System.getenv("KEY_ALIAS")
            val keyPassword = keystoreProperties.getProperty("KEY_PASSWORD") ?: System.getenv("KEY_PASSWORD")

            if (!keyStoreFilePath.isNullOrEmpty() && file(keyStoreFilePath).exists()) {
                storeFile = file(keyStoreFilePath)
                storePassword = keyStorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                initWith(getByName("debug"))
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-DIR/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Coil
    implementation(libs.coil.compose)

    // Gson
    implementation(libs.gson)

    debugImplementation(libs.androidx.ui.tooling)
}
