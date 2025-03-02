plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.juan.mygamingcollection"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.juan.mygamingcollection"
        minSdk = 32
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    configurations {
        //without excluding this in tests the firebase library checks that the app
        // should invoke FirebaseApp.initializeApp(Context) and you probably don't
        // want to run that on tests and just use the test libraries that create a
        // dummy app for you and sometimes even a dummy activity. That could be also
        // avoidable if you can isolate the firebase dependency from the module you are testing
        androidTestImplementation {
            exclude (group = "com.google.firebase', module: 'firebase-perf")
        }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            animationsDisabled = true
        }
    }
}

dependencies {
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4.android)
    //Accompanist Permissions from Google
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation (platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation (libs.firebase.bom)
    implementation(libs.firebase.storage.ktx)
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")
    implementation ("com.google.guava:guava:33.2.1-android")
    implementation(libs.firebase.storage)
    //Room
    val roomVersion = "2.6.1"
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    ////GSON
    implementation ("com.google.code.gson:gson:2.10.1")
    ////GLIDE
    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    /////////
    //Drawable Painter
    implementation ("com.google.accompanist:accompanist-drawablepainter:0.36.0")
    //ANDROID
    implementation(libs.androidx.appcompat)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation (libs.androidx.runtime.livedata)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.rxjava3)
    implementation ("androidx.credentials:credentials:1.3.0-alpha02")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation(libs.commons.validator)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation (libs.material3)
    implementation(libs.firebase.auth)
    implementation(libs.google.services)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.googleid)
    //TESTS
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //ADDEDNOW
    implementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test:runner:1.6.1")
    implementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    implementation("androidx.navigation:navigation-testing:2.8.3")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation ("androidx.test.espresso:espresso-idling-resource:3.5.1")
    ///UP TO HERE///
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //AWAITILITY INSTEAD OF THREAD.SLEEP(). ITS JAVA, then KOTLIN
    testImplementation ("org.awaitility:awaitility-kotlin:4.2.2")
    androidTestImplementation ("org.awaitility:awaitility-kotlin:4.2.2")
    testImplementation ("org.awaitility:awaitility-kotlin:4.2.2'")
    androidTestImplementation ("org.awaitility:awaitility-kotlin:4.2.2")
    //MOCKK
    testImplementation ("io.mockk:mockk:1.13.13")
    androidTestImplementation ("io.mockk:mockk-android:1.13.13")
    //MOCKITO
    implementation("org.mockito:mockito-core:5.14.2")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.4.0")
    //Robolectric
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.14")
    androidTestImplementation("org.robolectric:robolectric:4.14")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")
    testImplementation ("androidx.arch.core:core-common:2.2.0")
    testImplementation ("androidx.arch.core:core-runtime:2.2.0")
}
dependencies {
    androidTestImplementation("androidx.test:runner:1.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}