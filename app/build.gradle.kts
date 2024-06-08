plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}


android {
    namespace = "com.example.cepstun"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cepstun"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"201893687769-sr07ofrp9v95uek9u5md1dcr3qudto1c.apps.googleusercontent.com\"")
        buildConfigField("String", "BASE_URL_CURRENCY", "\"https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isShrinkResources = true
            isDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //SplashScreen
//    implementation(libs.androidx.core.splashscreen)

    // Library TensorFlow Lite
    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
//    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
//    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // for implementation ucrop
    implementation(libs.ucrop)

    // Custom Permission
    implementation (libs.dexter)

    // retrofit, gson and logging
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // lifecycle for implementation viewmodel to use livedata
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // android KTX activity and fragment for viewModels
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // view pager 2
    implementation (libs.androidx.viewpager2)

    // animation indicator
    implementation(libs.dotsindicator)

    // image circle
    implementation (libs.circleimageview)

    // glide for image from url
    implementation (libs.glide)

    // location now
    implementation (libs.play.services.location)
    implementation (libs.google.maps.services)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    // Facebook Auth
    implementation(libs.facebook.android.sdk)

    implementation(libs.androidx.library)

    // for dropdown
    implementation(libs.powerspinner)

    // location picker
    implementation(libs.play.services.maps)
    implementation (libs.android.maps.utils)
    implementation(libs.leku)


    // for retrofit support coroutine out of the box
//    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // for camera x
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // for implementation room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // data store
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)

    // for loading animation
    implementation(libs.lottie)

    // for image carousel
    implementation(libs.imageslideshow)
}