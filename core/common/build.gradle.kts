plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
    alias(libs.plugins.detekt)
}

jacoco {
    toolVersion = "0.8.12"
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    source.setFrom("src/main/java")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
