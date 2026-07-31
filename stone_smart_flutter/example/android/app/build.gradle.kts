plugins {
    id("com.android.application")
    id("kotlin-android")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "com.example.stone_smart_flutter_example"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = "27.0.12077973"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    signingConfigs {
        create("positivo") {
            storeFile = file("../../Chaves/positivo-keystore.jks")
            keyAlias = "platform"
            keyPassword = "654321"
            storePassword = "123456"
        }
        create("gertec") {
            storeFile = file("../../Chaves/Development_GertecDeveloper_CustomerAPP.jks")
            keyAlias = "developmentgertecdeveloper_customerapp"
            keyPassword = "Development@GertecDeveloper2018"
            storePassword = "Development@GertecDeveloper2018"
        }
    }

    lint {
        disable += "InvalidPackage"
    }

    packaging {
        resources {
            excludes += "META-INF/api_release.kotlin_module"
            excludes += "META-INF/client_release.kotlin_module"
        }
    }

    defaultConfig {
        // TODO: Specify your own unique Application ID (https://developer.android.com/studio/build/application-id.html).
        applicationId = "com.example.stone_smart_flutter_example"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = 22
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = "1.0"
    }

    buildTypes {
        release {
            isShrinkResources = false
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("gertec")
        }
        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("positivo")
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
    // No need to add kotlin-stdlib-jdk7 dependency as it's included by the plugin
}

