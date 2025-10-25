pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        // Android + Google plugins
        id("com.android.application") version "8.3.1" apply false
        id("com.google.gms.google-services") version "4.4.4" apply false

        // Kotlin (for Java interoperability and future Compose migration)
        id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    }
}

dependencyResolutionManagement {
    // Prefer repositories defined here instead of in project-level build files
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        google()
        mavenCentral()
    }
}

// Root project name
rootProject.name = "NeuraTask"

// Include the app module
include(":app")
