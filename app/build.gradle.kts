plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp) // 添加 kapt 插件
    alias(libs.plugins.hilt.android) // 添加 Hilt 插件
}

android {
    namespace = "com.example.joyclean"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.joyclean"
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
        compose = true
    }
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.6.0") // 或更高版本
    implementation("androidx.activity:activity:1.6.0")    // 或更高版本
    implementation("androidx.compose.runtime:runtime-saveable:1.5.0")
    implementation("com.google.dagger:hilt-android:2.48")
    implementation(libs.androidx.navigation.runtime.ktx)
    ksp("com.google.dagger:hilt-android-compiler:2.48")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.6.0-alpha01")
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    ksp ("androidx.room:room-compiler:$room_version")
    // room针对kotlin协程功能的扩展库
    implementation("androidx.room:room-ktx:$room_version")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.core.ktx)
}