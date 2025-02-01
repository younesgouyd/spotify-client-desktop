group = "dev.younesgouyd"
version = "0.1.0"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.jetbrains)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.logback)
    implementation(libs.json)
    implementation(libs.sqliteDriver)
    implementation(libs.sqldelightCoroutines)

    implementation(compose.desktop.currentOs) {
        exclude("org.jetbrains.compose.material") // todo - -_-
    }
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.logging)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentNegotiation)
    implementation(libs.ktor.serialization)
}

sqldelight {
    databases {
        create("SpotifyClientDesktop") {
            packageName.set("dev.younesgouyd.apps.spotifyclient.desktop.main.data.sqldelight")
        }
    }
}