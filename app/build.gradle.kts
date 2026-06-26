import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// ─── Keystore properties ──────────────────────────────────────────────────────
// Reads from keystore.properties locally, falls back to env vars on CI
// keystore.properties is in .gitignore — never committed
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

fun signingProp(envVar: String, propKey: String): String? =
    System.getenv(envVar) ?: keystoreProperties.getProperty(propKey)

val canSign = listOf(
    signingProp("KEYSTORE_FILE", "storeFile"),
    signingProp("KEYSTORE_PASSWORD", "storePassword"),
    signingProp("KEY_ALIAS", "keyAlias"),
    signingProp("KEY_PASSWORD", "keyPassword")
).all { it != null }
// ─────────────────────────────────────────────────────────────────────────────


android {
    namespace = "com.example.catalogapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.catalogapp"
        minSdk = 24
        targetSdk = 36

        // versionCode auto-increments from CI run number
        // Locally: always 1. On CI: run 1 → versionCode 1, run 47 → versionCode 47
        versionCode = System.getenv("GITHUB_RUN_NUMBER")?.toInt() ?: 1
        versionName = "1.0.${System.getenv("GITHUB_RUN_NUMBER") ?: "0"}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ─── Signing configs ──────────────────────────────────────────────────────
    signingConfigs {
        if (canSign) {
            create("release") {
                storeFile = file(signingProp("KEYSTORE_FILE", "storeFile")!!)
                storePassword = signingProp("KEYSTORE_PASSWORD", "storePassword")!!
                keyAlias = signingProp("KEY_ALIAS", "keyAlias")!!
                keyPassword = signingProp("KEY_PASSWORD", "keyPassword")!!
            }
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    // ─── Flavors ──────────────────────────────────────────────────────────────
    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }
        create("prod") {
            dimension = "environment"
            // No suffix — this is the real app
            buildConfigField("String", "BASE_URL", "\"https://fakestoreapi.com/\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "false")
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            isDebuggable = true
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = if (canSign) signingConfigs.findByName("release") else null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
