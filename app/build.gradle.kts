plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt") // KAPT for annotation processing
}

android {
    namespace = "com.example.lab_04"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lab_04"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Room dependencies
    implementation("androidx.room:room-runtime:2.6.0")
    implementation(libs.androidx.material3.android) // Room runtime
    kapt("androidx.room:room-compiler:2.6.0") // Room compiler for annotation processing
    implementation("androidx.room:room-ktx:2.6.0") // Room KTX for coroutines

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    // ViewModel and LiveData dependencies
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // ViewModel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2") // LiveData

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
