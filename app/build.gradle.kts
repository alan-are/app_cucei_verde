plugins {
    alias(libs.plugins.android.application)
    // Add the Google Services plugin ID here
    alias(libs.plugins.google.gms.googleServices) // Changed to use alias
    alias(libs.plugins.kotlin.kapt) // Changed to use alias
}

android {
    namespace = "cuceiverdecom.example"
    // Consider using the latest stable compileSdk if 35 is alpha/beta
    // and causing issues, otherwise 35 is fine if needed.
    compileSdk = 35

    defaultConfig {
        applicationId = "cuceiverdecom.example"
        minSdk = 28
        // targetSdk should generally match compileSdk
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
        // Keep source/target compatibility as needed, 11 is common.
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }    // Si estás usando View Binding o Data Binding, habilítalo aquí
    // buildFeatures {
    //    viewBinding = true
    // }
    
    // Habilitar BuildConfig para acceder a BuildConfig.DEBUG
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material) // This line already exists, likely from your version catalog
    implementation(libs.cardview) // Added CardView dependency
    implementation(libs.coordinatorlayout) // Added CoordinatorLayout
    implementation(libs.core.ktx) // Added AndroidX Core (includes NestedScrollView)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.material.v190)

    // Firebase Storage
    implementation(libs.firebase.storage) // Or implementation("com.google.firebase:firebase-storage-ktx") if not in version catalog

    // Glide for image loading
    implementation(libs.glide) // Or implementation("com.github.bumptech.glide:glide:4.16.0") if not in version catalog
    annotationProcessor(libs.glide.compiler) // For Java projects
    // If using Kotlin and kapt (which we are adding above):
    // kapt(libs.glide.compiler) // Uncomment if your project is primarily Kotlin



    // --- START: Added Firebase and Google Play Services Dependencies ---

    // Import the Firebase BoM (Bill of Materials) - manages versions
    // Check for the latest BoM version: https://firebase.google.com/docs/android/setup#available-libraries
    // Replace "33.1.1" with the actual latest version
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication
    implementation(libs.firebase.auth)

    // Firebase Firestore
    implementation(libs.firebase.firestore)

    // Firebase Realtime Database (if you are actually using it)
    implementation(libs.firebase.database)

    // Google Sign-In (part of Google Play Services)
    // Check for the latest version: https://developers.google.com/android/guides/setup
    // Replace "21.2.0" with the actual latest version
    implementation(libs.play.services.auth)

    // Google Play Services Location & Places
    implementation(libs.play.services.location)
    implementation(libs.places.sdk)

    // Note: play-services-tasks is usually included transitively by firebase-auth
    // You typically don't need to add it explicitly unless facing specific issues.
    // implementation("com.google.android.gms:play-services-tasks:VERSION")

    // --- END: Added Firebase and Google Play Services Dependencies ---


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// --- Optional: Best Practice with Version Catalogs (`libs.versions.toml`) ---
// Ideally, you would define these versions and dependencies in your
// gradle/libs.versions.toml file and reference them like:
//
// In libs.versions.toml:
// [versions]
// firebaseBom = "33.1.1"
// playServicesAuth = "21.2.0"
// ...
// [libraries]
// firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
// firebase-auth = { group = "com.google.firebase", name = "firebase-auth" } // Version managed by BoM
// firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" } // Version managed by BoM
// firebase-database = { group = "com.google.firebase", name = "firebase-database" } // Version managed by BoM
// play-services-auth = { group = "com.google.android.gms", name = "play-services-auth", version.ref = "playServicesAuth" }
// ...
//
// Then in this build.gradle.kts:
// implementation(platform(libs.firebase.bom))
// implementation(libs.firebase.auth)
// implementation(libs.firebase.firestore)
// implementation(libs.firebase.database)
// implementation(libs.play.services.auth) // Note: Adjust alias as needed (e.g., play-services-auth might become libs.playServices.auth)
// -----------------------------------------------------------------------------