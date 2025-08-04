plugins {
    alias(libs.plugins.android.application)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "de.thm.mixit"
    compileSdk = 36

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            pickFirsts += listOf(
                "META-INF/DEPENDENCIES"
            )
        }
    }

    defaultConfig {
        applicationId = "de.thm.mixit"
        minSdk = 24
        targetSdk = 36
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

        debug {
            enableUnitTestCoverage = true
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    secrets {
        defaultPropertiesFileName = "local.defaults.properties"
    }
}

dependencies {
    implementation(libs.recyclerview)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.openai.java)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.flexbox)
    implementation(libs.fragment)
    implementation(libs.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}