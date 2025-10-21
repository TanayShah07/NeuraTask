plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.neeravtanay.neuratask"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.neeravtanay.neuratask"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Firebase BoM - manages all Firebase library versions automatically
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // Firebase core services
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Cloud Messaging (optional for notifications)
    implementation("com.google.firebase:firebase-messaging")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // UI + Material
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Optional: Rounded images (for logo/profile)
    implementation("de.hdodenhof:circleimageview:3.1.0")
}
