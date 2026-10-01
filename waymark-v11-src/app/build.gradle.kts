plugins {
    id("com.android.application")
}

android {
    namespace = "com.nakuul.fieldkeep"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nakuul.waymarkfieldnotes"
        minSdk = 26
        targetSdk = 35
        versionCode = 8
        versionName = "0.8-reminders-labels-theme-test"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.activity:activity:1.10.0")
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.7")
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")
    implementation("androidx.camera:camera-video:1.4.1")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
}
