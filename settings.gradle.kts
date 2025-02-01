rootProject.name = "spotify-client-desktop"

include(":main")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val versions = object {
                val java = version("java", "22")
                val kotlin = version("kotlin", "2.0.20")
                val ktor = version("ktor", "3.0.3")
                val coroutines = version("coroutines", "1.10.1")
                val logback = version("logback", "1.5.16")
                val compose = version("compose", "1.7.3")
                val json = version("json", "20250107")
                val sqldelight = version("sqldelight", "2.0.2")
            }

            plugin("kotlin.jvm", "org.jetbrains.kotlin.jvm").versionRef(versions.kotlin)
            plugin("kotlin.serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef(versions.kotlin)
            plugin("compose.jetbrains", "org.jetbrains.compose").versionRef(versions.compose)
            plugin("compose.compiler", "org.jetbrains.kotlin.plugin.compose").versionRef(versions.kotlin)
            plugin("sqldelight", "app.cash.sqldelight").versionRef(versions.sqldelight)

            library("coroutines.core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef(versions.coroutines)

            library("logback", "ch.qos.logback", "logback-classic").versionRef(versions.logback)
            library("json", "org.json", "json").versionRef(versions.json)
            library("sqliteDriver", "app.cash.sqldelight", "sqlite-driver").versionRef(versions.sqldelight)
            library("sqldelightCoroutines", "app.cash.sqldelight", "coroutines-extensions").versionRef(versions.sqldelight)

            library("ktor.server.core", "io.ktor", "ktor-server-core").versionRef(versions.ktor)
            library("ktor.server.netty", "io.ktor", "ktor-server-netty").versionRef(versions.ktor)
            library("ktor.server.logging", "io.ktor", "ktor-server-call-logging").versionRef(versions.ktor)

            library("ktor.client.core", "io.ktor", "ktor-client-core").versionRef(versions.ktor)
            library("ktor.client.cio", "io.ktor", "ktor-client-cio").versionRef(versions.ktor)
            library("ktor.client.logging", "io.ktor", "ktor-client-logging").versionRef(versions.ktor)
            library("ktor.client.contentNegotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(versions.ktor)

            library("ktor.serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(versions.ktor)
        }
    }
}