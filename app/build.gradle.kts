plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.deixebledenkaito.despertapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.deixebledenkaito.despertapp"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//EM AFEGIT AIXO PER EL ROOM
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    //EM AFEGIT AIXO PER EL ROOM
    kapt {
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.databinding.adapters)
    implementation(libs.androidx.games.activity)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.compose.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    FIREBASE
    implementation (libs.firebase.auth.ktx)
    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.firestore.ktx)  // Firestore
    implementation (libs.firebase.crashlytics)
    implementation (libs.firebase.analytics) // opcional però recomanat

    // Hilt (DI)
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)

    // Per a selecció d'imatges  COIL
    implementation (libs.coil.compose)

    //MATERIAL 3 ICONS
    implementation(libs.material3)
    implementation (libs.androidx.material.icons.core)
    implementation (libs.androidx.material.icons.extended)

//ANIMATION
    implementation(libs.androidx.animation)

    // Room
    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

// Lifecycle & ViewModel
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    // Logging
    implementation (libs.androidx.runtime.livedata)

    // Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    implementation (libs.accompanist.systemuicontroller)

    //Poder lleguir JSON
    implementation (libs.gson)

    // Afegir a les dependencies de l'app
    implementation (libs.androidx.work.runtime.ktx)

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

//EXPOTAR AUDIOS
    implementation(libs.androidx.media3.exoplayer)
}