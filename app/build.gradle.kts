plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ady.studybuddy5"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ady.studybuddy5"
        minSdk = 24
        targetSdk = 34
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.viewfinder.core)
    implementation(libs.androidx.ui.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose dependencies
    implementation("androidx.compose.ui:ui:1.7.6") // Add Compose UI
    implementation("androidx.compose.foundation:foundation:1.7.6") // Add Compose Foundation
    implementation("androidx.compose.material3:material3:1.3.1") // Add Compose Material3
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.6") // Add tooling for Compose preview
    implementation("androidx.activity:activity-compose:1.9.3") // Add Compose support for activities

}
