// File: app/build.gradle.kts
// This file configures dependencies and settings specific to the app module.

plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.ecostayretreat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ecostayretreat"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Enable desugaring for newer Java APIs on older Android versions.
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        // Enables View Binding for safer and easier view access.
        viewBinding = true
    }
}

dependencies {
    // Firebase Bill of Materials (BoM) - manages versions for Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")

    // Material Design 3 Components
    implementation("com.google.android.material:material:1.12.0")

    // AndroidX Core Libraries
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Navigation Component for fragment navigation
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // ViewModel and LiveData for MVVM architecture
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Lottie for animations
    implementation("com.airbnb.android:lottie:6.4.0")

    // Desugaring for Java 8+ APIs
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Test dependencies (optional but good practice)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

// Apply the Google services plugin at the end
apply(plugin = "com.google.gms.google-services")