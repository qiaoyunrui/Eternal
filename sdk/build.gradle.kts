import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(ext("compileSdkVersion") as Int)

    defaultConfig {

        minSdkVersion(ext("minSdkVersion") as Int)
        targetSdkVersion(ext("targetSdkVersion") as Int)
        versionCode = ext("globalVersionCode").toString().toInt()
        versionName = "${ext("globalVersionName")}"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    testImplementation("junit:junit:${ext("junitVersion")}")
    androidTestImplementation("com.android.support.test:runner:${ext("runnerVersion")}")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:${ext("espressoVersion")}")
}

fun ext(key: String): Any? = rootProject.ext[key]

task("printVersionInfo") {
    println("versionCode: ${ext("globalVersionCode")}\tversionName: ${ext("globalVersionName")}")
}