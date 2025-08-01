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

// FIXME: As of Java 21 dynamically loading agents is not recommended.
//  Currently there is no good solution to fix this with gradle.
//  This is a more or less optimal workaround copied from here:
//  https://github.com/mockito/mockito/issues/3037#issuecomment-2724136224
//  also mentioned in the docs
//  https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html#0.3
val mockitoAgent = configurations.create("mockitoAgent")
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
    testImplementation(libs.mockito.core)
    @Suppress("UnstableApiUsage")
    mockitoAgent(libs.mockito.core) { isTransitive = false }
    testImplementation(libs.core.testing)
    testImplementation(libs.lifecycle.viewmodel.android)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

tasks.withType<Test> {
        jvmArgumentProviders.add(
            objects.newInstance<JavaAgentArgumentProvider>().apply {
        classpath.from(mockitoAgent)
    })
}
abstract class JavaAgentArgumentProvider : CommandLineArgumentProvider {
    @get:Classpath
    abstract val classpath: ConfigurableFileCollection

    override fun asArguments() = listOf("-javaagent:${classpath.singleFile.absolutePath}")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}