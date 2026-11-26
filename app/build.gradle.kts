plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cz.kuvag.sms"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.kuvag.sms"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
