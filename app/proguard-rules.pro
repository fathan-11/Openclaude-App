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
