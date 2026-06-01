# Add project specific ProGuard rules here.
-keep class com.openclaude.android.data.remote.dto.** { *; }
-keep class com.openclaude.android.data.model.** { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
