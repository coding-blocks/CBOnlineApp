# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-ignorewarnings

# ======= Keep Fragment Names =========
-keepnames class * extends androidx.fragment.app.Fragment

# ======= Models =========
-keep class com.codingblocks.cbonlineapp.database.models.*** { *; }
# ======= Models =========


# ========= Kotlin ===========
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers public class com.mypackage.** {
    public synthetic <methods>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
# ====== Jackson =========

# Proguard configuration for Jackson 2.x
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
# Proguard configuration for Jackson 2.x
-dontwarn com.fasterxml.jackson.databind.**
-keepclassmembers class * {
     @com.fasterxml.jackson.annotation.* *;
}
# ====== Jackson =========

#  ==== JSONAPI Converter ====
# Keep jsonapi-converter relative fields
-keep class com.github.jasminb.** { *; }
-keepclassmembers class * {
    @com.github.jasminb.jsonapi.annotations.Id <fields>;
    @com.github.jasminb.jsonapi.annotations.Meta <fields>;
    @com.github.jasminb.jsonapi.annotations.Type <fields>;
    @com.github.jasminb.jsonapi.annotations.Relationship <fields>;
    @com.github.jasminb.jsonapi.annotations.Links <fields>;
}

-keep interface com.github.jasminb.jsonapi.**Constants { *; }
-keep class com.github.jasminb.jsonapi.**Constants {*;}
-keep interface com.github.jasminb.jsonapi.**Type { *; }
-keep class com.github.jasminb.jsonapi.**Type {*;}
# Keep custom id handlers
-keep class * implements com.github.jasminb.jsonapi.ResourceIdHandler

# Our Models
-keep class com.codingblocks.onlineapi.models.*** { *; }

#  ==== JSONAPI Converter ====

# ===== GMS ====
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
# ===== GMS ====

# ===== VIDEO CIPHER =====
-keep class com.vdocipher.aegis.* { *; }

# for prettytime
-keep class com.ocpsoft.pretty.time.i18n.**
-keep class org.ocpsoft.prettytime.i18n.**
-keepnames class ** implements org.ocpsoft.prettytime.TimeUnit

# ==== RAZORPAY ====
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}

-optimizations !method/inlining/*

-keepclasseswithmembers class * {
  public void onPayment*(...);
}





