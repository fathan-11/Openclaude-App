# ============================================================
# ProGuard rules for OpenClaude Android
# ============================================================

# --- General Kotlin ---
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses,EnclosingMethod

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# --- Retrofit ---
# Keep Retrofit interface methods and annotations
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Retrofit response types
-keepattributes Signature
-keepattributes Exceptions

# --- Moshi ---
# Keep Moshi model fields and annotations
-keep class com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class com.openclaude.android.data.** { *; }
-keepclassmembers class com.openclaude.android.domain.** { *; }

# Moshi Kotlin codegen
-keep class **JsonAdapter { *; }
-keep class **JsonAdapter$* { *; }

# --- Hilt / Dagger ---
# Keep inject constructors and modules
-keepclassmembers,allowshrinking,allowobfuscation class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @javax.inject.Inject <fields>;
}
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-dontwarn dagger.hilt.**

# --- Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}
-dontwarn androidx.room.**

# --- DataStore ---
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}

# --- Compose ---
-dontwarn androidx.compose.**

# --- AndroidX ---
-keep class androidx.lifecycle.** { *; }
-keep class androidx.navigation.** { *; }

# --- OpenClaude app models (keep all data classes for serialization) ---
-keep class com.openclaude.android.data.model.** { *; }
-keep class com.openclaude.android.data.remote.dto.** { *; }
-keep class com.openclaude.android.data.local.** { *; }

# --- Enum safety ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Parcelable ---
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
