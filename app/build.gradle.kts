plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.practicum.playlistmaker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.practicum.playlistmaker"
        minSdk = 29
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

    dependencies {
        kapt(libs.moxy.compiler)
        kapt(libs.room.compiler)

        implementation(libs.peko)
        implementation(libs.room.runtime)
        implementation(libs.room.ktx)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.navigation.fragment)
        implementation(libs.navigation.ui)
        implementation(libs.androidx.fragment.ktx)
        implementation(libs.moxy)
        implementation(libs.moxy.android)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.viewpager2)
        implementation(libs.androidx.viewbinding)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.material)
        implementation(libs.retrofit)
        implementation(libs.retrofit.gson)
        implementation(libs.gson)
        implementation(libs.glide)
        implementation(libs.koin.android)

        annotationProcessor(libs.glide.compiler)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }
}
