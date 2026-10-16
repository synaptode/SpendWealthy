plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register<Exec>("runIos") {
    group = "application"
    description = "Boots the iOS simulator and launches the SpendWealthy app"
    commandLine("./run.sh", "ios")
}

tasks.register<Exec>("runAndroid") {
    group = "application"
    description = "Boots the Android emulator/device and launches the SpendWealthy app"
    commandLine("./run.sh", "android")
}