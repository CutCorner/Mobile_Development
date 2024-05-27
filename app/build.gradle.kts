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
//        buildConfigField("String", "MAPS_API_KEY", "\"${System.getenv("MAPS_API_KEY") ?: ""}\"")
//        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
//        buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"201893687769-sr07ofrp9v95uek9u5md1dcr3qudto1c.apps.googleusercontent.com\"")
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

//            buildConfigField("String", "CURRENCY_API_KEY", "\"cur_live_UxgQfhhwri7VJYT0Vm2cIs1HC7uiIzSMwFeRBHnz\"")
//            buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCJAIEnEeP4SZVeaO0vdyj68CgBtkL_FD8\"")
//            buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"201893687769-oe53ii5e6ee0ggmjo5kjgn2poassuo4b.apps.googleusercontent.com\"") sesat njir

        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
//            buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"201893687769-sr07ofrp9v95uek9u5md1dcr3qudto1c.apps.googleusercontent.com\"")
//            buildConfigField("String", "BASE_URL_CURRENCY", "\"https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/\"")
//            buildConfigField("String", "CURRENCY_API_KEY", "\"cur_live_UxgQfhhwri7VJYT0Vm2cIs1HC7uiIzSMwFeRBHnz\"")
//            buildConfigField("String", "MAPS_API_KEY", "\"AIzaSyCJAIEnEeP4SZVeaO0vdyj68CgBtkL_FD8\"")
//            buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"201893687769-oe53ii5e6ee0ggmjo5kjgn2poassuo4b.apps.googleusercontent.com\"") sesat njir
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
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
//    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
//    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    // for implementation ucrop
    implementation("com.github.yalantis:ucrop:2.2.9")

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
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.play.services.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation ("com.google.firebase:firebase-storage-ktx:20.3.0")

    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.databinding:library:3.2.0-alpha11")

    // for dropdown
    implementation(libs.powerspinner)

    // location picker
    implementation(libs.play.services.maps)
    implementation ("com.google.maps.android:android-maps-utils:3.8.2")
    implementation("com.adevinta.android:leku:11.1.4")


    // for retrofit support coroutine out of the box
//    implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")

    // for camera x
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")

    // for loading animation
    implementation("com.airbnb.android:lottie:6.4.0")
}