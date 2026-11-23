# Proguard rules for Regain
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class ai.regain.app.data.** { *; }
