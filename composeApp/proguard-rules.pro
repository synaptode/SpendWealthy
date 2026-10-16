# ==============================================================================
# SpendWealthy — Enterprise Proguard / R8 Optimization Rules
# ==============================================================================

# Kotlin Multiplatform Primitives & Coroutines
-keepattributes *Annotation*, InnerClasses, Signature, Exception, SourceFile, LineNumberTable
-keepclassmembers class * extends kotlin.coroutines.jvm.internal.ContinuationImpl {
    <fields>;
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowoptimization class * {
    @kotlinx.serialization.Serializable class *;
}

# SQLDelight
-keep class com.spendwealthy.app.db.** { *; }
-keep interface app.cash.sqldelight.** { *; }
-dontwarn app.cash.sqldelight.**

# Voyager Navigation & ScreenModel
-keep class * implements cafe.adriel.voyager.core.screen.Screen { *; }
-keep class * extends cafe.adriel.voyager.core.model.ScreenModel { *; }
-dontwarn cafe.adriel.voyager.**

# Koin Dependency Injection
-dontwarn io.insert.koin.**
-keep class io.insert.koin.** { *; }

# AndroidX Cryptography & Biometric
-keep class androidx.biometric.** { *; }
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# Compose Multiplatform Runtime
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**
