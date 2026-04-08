// File: build.gradle.kts (Root of your project)
// This file defines the plugins and versions used across all modules.

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("androidx.navigation.safeargs") version "2.7.7" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}