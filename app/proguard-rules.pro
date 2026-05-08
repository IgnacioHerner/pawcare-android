# Firebase / Firestore
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }

# Kotlin data classes usadas por Firestore
-keepclassmembers class com.ignaherner.pawcare.domain.model.** { *; }
-keepclassmembers class com.ignaherner.pawcare.data.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# Crash reports legibles
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile