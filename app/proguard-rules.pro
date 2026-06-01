<<<<<<< HEAD
# Add project specific ProGuard rules here.
-keep class com.openclaude.android.data.remote.dto.** { *; }
-keep class com.openclaude.android.data.model.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
=======
# Moshi
-keepclassmembers class com.example.repopattern.data.model.** { *; }
-keep class com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
>>>>>>> origin/main
