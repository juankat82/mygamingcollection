buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath ("com.google.gms:google-services:4.4.1")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}

