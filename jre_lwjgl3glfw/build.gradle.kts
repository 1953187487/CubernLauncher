plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.movtery.zalithlauncher3.lwjgl"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // LWJGL dependencies will be added from the libs folder
    implementation(files("libs/lwjgl.jar"))
    implementation(files("libs/lwjgl-glfw.jar"))
    implementation(files("libs/lwjgl-opengl.jar"))
    implementation(files("libs/lwjgl-openal.jar"))
}
