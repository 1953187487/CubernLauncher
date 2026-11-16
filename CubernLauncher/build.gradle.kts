plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose") version "1.5.10"
}

android {
    namespace = "com.movtery.cubernlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.movtery.cubernlauncher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-26.2+"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }

        buildConfigField("String", "LAUNCHER_APP_NAME", "\"${project.property("launcher_app_name")}\"")
        buildConfigField("String", "LAUNCHER_NAME", "\"${project.property("launcher_name")}\"")
        buildConfigField("int", "LAUNCHER_VERSION_CODE", project.property("launcher_version_code").toString())
        buildConfigField("String", "LAUNCHER_VERSION_NAME", "\"${project.property("launcher_version_name")}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

        buildConfigField("String", "LAUNCHER_APP_NAME", "\"${project.property("launcher_app_name")}\"")
        buildConfigField("String", "LAUNCHER_NAME", "\"${project.property("launcher_name")}\"")
        buildConfigField("int", "LAUNCHER_VERSION_CODE", project.property("launcher_version_code").toString())
        buildConfigField("String", "LAUNCHER_VERSION_NAME", "\"${project.property("launcher_version_name")}\"")
    }

    buildTypes {
        release {
            minifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            minifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // MUIX Compose UI
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.4")
    
    // Compose Multiplatform
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material3)
    
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // LWJGL Support
    project(":jre_lwjgl3glfw")
}
