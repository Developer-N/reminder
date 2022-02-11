buildscript {
    val compose_version by extra("1.0.1")
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath(kotlin("gradle-plugin", version = "1.6.10"))
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
task("clean") {
    delete(rootProject.buildDir)
}