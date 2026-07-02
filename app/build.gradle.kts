import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
    jacoco
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
    implementation(project(":data:product"))
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ─── JaCoCo configuration ─────────────────────────────────────────────────────
jacoco {
    toolVersion = "0.8.12"
}

// Enable coverage data collection during unit tests
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// ─── JaCoCo report task ───────────────────────────────────────────────────────
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(
        "testDevDebugUnitTest",
        ":domain:product:test",
        ":core:common:test",
        ":data:product:testDebugUnitTest"
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*",
        "**/*Hilt*.*", "**/*Dagger*.*", "**/*_Factory*.*",
        "**/*MembersInjector*.*", "**/di/**",
        "**/hilt_aggregated_deps/**",
        "**/dagger/hilt/**",
        "**/*Database*.*",
        "**/*Database_Impl*.*",
        // Exclude Kotlin compiler-generated lambda/coroutine synthetic classes
        "**/*\$*\$*.*",
        "**/*\$inlined*.*"
    )

    val appTree = fileTree(layout.buildDirectory.get()) {
        include(
            "tmp/kotlin-classes/devDebug/**/*.class",
            "intermediates/javac/devDebug/**/*.class"
        )
        exclude(fileFilter)
    }

    val domainTree = fileTree(project(":domain:product").layout.buildDirectory.get()) {
        include("classes/kotlin/main/**/*.class")
        exclude(fileFilter)
    }

    val coreCommonTree = fileTree(project(":core:common").layout.buildDirectory.get()) {
        include("classes/kotlin/main/**/*.class")
        exclude(fileFilter)
    }

    val dataTree = fileTree(project(":data:product").layout.buildDirectory.get()) {
        include("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes/**/*.class")
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(
        files(
            "src/main/java",
            "${project(":domain:product").projectDir}/src/main/java",
            "${project(":core:common").projectDir}/src/main/java",
            "${project(":data:product").projectDir}/src/main/java"
        )
    )

    classDirectories.setFrom(files(appTree, domainTree, coreCommonTree, dataTree))

    executionData.setFrom(
        files(
            fileTree(layout.buildDirectory.get()) {
                include("outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec")
            },
            fileTree(project(":domain:product").layout.buildDirectory.get()) {
                include("jacoco/test.exec")
            },
            fileTree(project(":core:common").layout.buildDirectory.get()) {
                include("jacoco/test.exec")
            },
            fileTree(project(":data:product").layout.buildDirectory.get()) {
                include("jacoco/testDebugUnitTest.exec")
            }
        )
    )
}

// ─── Coverage threshold enforcement ───────────────────────────────────────────
tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    violationRules {
        // Overall app threshold — excludes generated/untestable code via fileFilter
        rule {
            limit {
                minimum = "0.55".toBigDecimal()
            }
        }

        // Domain layer — pure business logic, highest bar
        rule {
            element = "CLASS"
            includes = listOf("com.example.catalogapp.domain.*")
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }

        // Repository — critical class, must test error paths
        rule {
            element = "CLASS"
            includes = listOf("com.example.catalogapp.data.product.repository.*")
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }

        // Mapper — simple pure functions, high bar achievable
        rule {
            element = "CLASS"
            includes = listOf("com.example.catalogapp.data.product.mapper.*")
            limit {
                minimum = "0.85".toBigDecimal()
            }
        }
    }

    val fileFilter = listOf(
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*",
        "**/Manifest*.*", "**/*Test*.*", "android/**/*.*",
        "**/*Hilt*.*", "**/*Dagger*.*", "**/*_Factory*.*",
        "**/hilt_aggregated_deps/**",
        "**/dagger/hilt/**",
        "**/dao/**",
        "**/*Database*.*",
        "**/*Database_Impl*.*",
        // Exclude Kotlin compiler-generated lambda/coroutine synthetic classes
        "**/*\$*\$*.*",
        "**/*\$inlined*.*"
    )

    // Collect classes from all modules
    val appTree = fileTree(layout.buildDirectory.get()) {
        include(
            "tmp/kotlin-classes/devDebug/**/*.class",
            "intermediates/javac/devDebug/**/*.class"
        )
        exclude(fileFilter)
    }

    val domainTree = fileTree(project(":domain:product").layout.buildDirectory.get()) {
        include("classes/kotlin/main/**/*.class")
        exclude(fileFilter)
    }

    val coreCommonTree = fileTree(project(":core:common").layout.buildDirectory.get()) {
        include("classes/kotlin/main/**/*.class")
        exclude(fileFilter)
    }

    val dataTree = fileTree(project(":data:product").layout.buildDirectory.get()) {
        include("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes/**/*.class")
        exclude(fileFilter)
    }

    sourceDirectories.setFrom(
        files(
            "src/main/java",
            "${project(":domain:product").projectDir}/src/main/java",
            "${project(":core:common").projectDir}/src/main/java",
            "${project(":data:product").projectDir}/src/main/java"
        )
    )

    classDirectories.setFrom(files(appTree, domainTree, coreCommonTree, dataTree))

    executionData.setFrom(
        files(
            fileTree(layout.buildDirectory.get()) {
                include("outputs/unit_test_code_coverage/devDebugUnitTest/testDevDebugUnitTest.exec")
            },
            fileTree(project(":domain:product").layout.buildDirectory.get()) {
                include("jacoco/test.exec")
            },
            fileTree(project(":core:common").layout.buildDirectory.get()) {
                include("jacoco/test.exec")
            },
            fileTree(project(":data:product").layout.buildDirectory.get()) {
                include("jacoco/testDebugUnitTest.exec")
            }
        )
    )
}

// ─── Detekt configuration ──────────────────────────────────────────────────
detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = false
    source.setFrom("src/main/java", "src/main/kotlin")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(false)
        sarif.required.set(false)
    }
}
