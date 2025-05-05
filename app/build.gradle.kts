plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "cuceiverdecom.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "cuceiverdecom.example"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")

    implementation(libs.material.v190) // Diseño Material
    implementation(libs.core) // Compatibilidad
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity.ktx)

    implementation(platform(libs.firebase.bom.v3231))

    // Firebase sin BoM (especifica versiones manualmente si prefieres)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.google.firebase.analytics.ktx)
    implementation(libs.firebase.database.ktx)

    // Glide para manejo de imágenes
    implementation(libs.glide)
    implementation(libs.firebase.firestore)
    annotationProcessor(libs.compiler)

    // Google Play Services
    implementation(libs.play.services.auth.v2070)

    // Para selección de imágenes
    implementation(libs.exifinterface)

    // Para manejo de ubicación (opcional)
    implementation(libs.play.services.location)

    implementation(libs.credentials.v120)
    implementation(libs.credentials.play.services.auth.v120)
}