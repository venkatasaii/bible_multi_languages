# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# App data models used with kotlinx.serialization (reflection-based discovery)
-keep,includedescriptorclasses class com.kuchiveapps.bibleapp.data.** { *; }
-keepclassmembers class com.kuchiveapps.bibleapp.data.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# Google ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.mlkit.**

# Compose: keep all Composable functions and tooling annotations
-keep class androidx.compose.runtime.** { *; }
-keepclasseswithmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Crash report readability
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
