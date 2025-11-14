import com.android.build.api.dsl.Packaging
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    //id("kotlin-kapt")
}

android {
    namespace = "com.example.drawingapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.drawingapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val keystoreFile = project.rootProject.file("secrets.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        // if the key doesn't exist, return a blank string/key
        val apiKey = properties.getProperty("API_KEY") ?: ""
        buildConfigField("String", "API_KEY", apiKey)
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
        dataBinding = true
        buildConfig = true
    }
    packagingOptions {
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/INDEX.LIST")
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

    implementation("androidx.navigation:navigation-compose:2.9.5")
    implementation(libs.androidx.ui.unit)
    implementation(libs.androidx.runtime)
    //implementation(libs.androidx.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.ui.graphics)
    ksp(libs.room.compiler)
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.6")


    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Gemini helped with these dependencies

    // 1. The official Google Cloud Vision client library for Java/Kotlin
    implementation("com.google.cloud:google-cloud-vision:3.77.0")

    // 2. The authentication library for using API Keys
    implementation("com.google.auth:google-auth-library-oauth2-http:1.40.0")

    // 3. The Cloud Vision library requires gRPC and Protobuf for communication
    implementation("io.grpc:grpc-okhttp:1.76.0")
    implementation("io.grpc:grpc-stub:1.76.0")
    implementation("io.grpc:grpc-protobuf-lite:1.76.0") {
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
    }

    // For converting Bitmaps to ByteString
    implementation("com.google.protobuf:protobuf-java:4.33.1")

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio) // or use .android instead
    implementation(libs.ktor.client.android) // or use .android instead
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)


}
