plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
