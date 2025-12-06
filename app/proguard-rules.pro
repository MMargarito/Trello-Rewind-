# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================
# KANHERO APP - PROGUARD RULES
# ============================================

# Keep Room entities and DAOs
-keep class com.trellorewind.app.data.entity.** { *; }
-keep class com.trellorewind.app.data.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Keep Google Play Billing classes
-keep class com.android.billingclient.api.** { *; }
-keepclassmembers class com.android.billingclient.api.** { *; }

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep data classes used for serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

