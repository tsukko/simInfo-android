import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

var keystoreProperties = Properties()
var keystorePropertiesFile = rootProject.file("../keystore.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "jp.co.integrityworks.mysiminfo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "jp.co.integrityworks.mysiminfo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 15
        versionName = "2.1"

        androidResources {
            localeFilters += listOf("en", "ja")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".deb"
            isDebuggable = true
            isMinifyEnabled = false
            manifestPlaceholders["admob_app_id"] = project.findProperty("admobAppIdSample") as String
            buildConfigField(
                "String",
                "admob_app_id",
                '"' + "${project.findProperty("admobAppIdSample") ?: ""}" + '"'
            )
            resValue(
                "string",
                "ad_unit_id",
                project.findProperty("admobBannerSample") as String
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            ndk {
                debugSymbolLevel = "FULL"
            }
            manifestPlaceholders["admob_app_id"] =
                project.findProperty("admobAppIdStoragePath") as String
            buildConfigField(
                "String",
                "admob_app_id",
                '"' + "${project.findProperty("admobAppIdStoragePath") ?: ""}" + '"'
            )
            resValue(
                "string",
                "ad_unit_id",
                project.findProperty("admobBannerStoragePath") as String
            )
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
    // New DSL for Kotlin in AGP 9.0+
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.compose.material3)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(libs.compose.foundation)
    implementation(libs.compose.icons)
    implementation(libs.compose.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.ads)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
