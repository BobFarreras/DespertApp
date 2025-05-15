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
        minSdk = 25
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
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.animation.android)
    implementation(libs.androidx.animation.android)
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
}