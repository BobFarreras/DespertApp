plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    kotlin("kapt")
    alias(libs.plugins.androidHilt)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.deixebledenkaito.despertapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.deixebledenkaito.despertapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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


    // Hilt (DI)
    implementation (libs.hilt.android)
    kapt (libs.hilt.compiler)
    implementation (libs.androidx.hilt.navigation.compose)

    // Per a selecció d'imatges  COIL
    implementation ("io.coil-kt:coil-compose:2.4.0")

    //MATERIAL 3 ICONS
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation (libs.androidx.material.icons.core)
    implementation (libs.androidx.material.icons.extended)

//ANIMATION
    implementation("androidx.compose.animation:animation:1.8.1")

    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

// Lifecycle & ViewModel
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Logging
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.5")

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    //Poder lleguir JSON
    implementation ("com.google.code.gson:gson:2.10.1")

    // Afegir a les dependencies de l'app
    implementation ("androidx.work:work-runtime-ktx:2.7.1")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}