plugins {

    id("com.android.application")
    id("com.google.gms.google-services")

}

android {
    namespace = "cuceiverdecom.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "cuceiverdecom.example"
        minSdk = 28
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)

    // Firebase (el BOM maneja las versiones)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.database)

    // Google Sign-In
    implementation(libs.play.services.auth)

    // Credentials API
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)

    implementation("com.google.firebase:firebase-firestore-ktx")
}