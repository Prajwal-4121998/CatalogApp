plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
