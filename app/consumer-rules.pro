# ===================================================================
# AquaSafe Monitor — ProGuard/R8 Consumer Rules
# Keep rules bundled into the APK for downstream builds.
# ===================================================================

# --- kotlinx.serialization ---------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-keep,includedescriptorclasses class com.aquasafe.monitor.**$$serializer { *; }
-keepclassmembers class com.aquasafe.monitor.** {
    *** Companion;
}
-keepclasseswithmembers class com.aquasafe.monitor.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ===================================================================
# Ktor — keep engine factory and service loader entries
# ===================================================================
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Keep Ktor client engine ServiceLoader
-keep class io.ktor.client.engine.** { *; }
-keep class io.ktor.serialization.** { *; }

# --- Coroutines --------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- OkHttp (Ktor engine) ----------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# --- osmdroid ----------------------------------------------------------------
-keep class org.osmdroid.** { *; }
-dontwarn org.osmdroid.**
