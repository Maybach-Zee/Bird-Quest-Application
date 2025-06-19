plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.birdquestapplicationfinal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.birdquestapplicationfinal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding{
            enable = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1") // Latest as of Nov 2024
    implementation("androidx.appcompat:appcompat:1.7.0") // Latest as of Nov 2024
    implementation("com.google.android.material:material:1.12.0") // Latest as of Nov 2024
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Latest stable

    testImplementation("junit:junit:4.13.2") // Latest stable
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // Latest as of Nov 2024
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Latest as of Nov 2024

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:23.1.0") // Latest stable
    implementation("com.google.firebase:firebase-firestore:25.1.1") // Latest stable
    implementation("com.google.firebase:firebase-database:21.0.0") // Latest stable
    implementation("com.google.firebase:firebase-appcheck:17.1.1") // Latest stable

    // Google Play Services dependencies
    implementation("com.google.android.gms:play-services-location:21.0.1") // Latest stable
    implementation("com.google.android.gms:play-services-maps:18.1.0") // Updated version
    implementation("com.google.code.gson:gson:2.10.1") // Latest stable

    // Mapbox dependencies
    implementation("com.mapbox.maps:android:10.16.1") // Latest stable
    implementation("com.mapbox.navigation:android:2.10.1") // Latest stable
    implementation("com.mapbox.navigation:ui-dropin:2.15.3") // Latest stable
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:6.10.0") // Latest stable
}
