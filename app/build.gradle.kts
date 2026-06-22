plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.legacy.kapt)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.durranitech.salahsync"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.durranitech.salahsync"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.tv.material)
    implementation(libs.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.benchmark.traceprocessor)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Room (KSP)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.androidx.room.compiler)

    //Icons
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material)


    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)


    //Testing

    // JUnit 4 - The testing framework

    // Kotlin Coroutines Test - For testing StateFlow/Flow (REQUIRED for you)
    testImplementation(libs.kotlinx.coroutines.test)

    // AndroidX Test - Core testing utilities

    // Compose UI Testing (for Compose screens)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // QR Scanning (Camera + ML Kit)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.accompanist.permissions)

    // QR Generation
    implementation(libs.zxing.core)

    //FCM
    implementation(libs.firebase.messaging)

    // Barcode Scanning (Camera + ML Kit)
    implementation(libs.androidx.camera.camera2.v141)
    implementation(libs.androidx.camera.lifecycle.v141)
    implementation(libs.androidx.camera.view.v141)


}