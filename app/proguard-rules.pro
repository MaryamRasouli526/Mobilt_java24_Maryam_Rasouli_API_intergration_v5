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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Behåll generisk typinformation för Retrofit/Gson
-keepattributes Signature

# Behåll modeller för Gson
-keep class se.gritacademy.maryam.rasouli.malmo.api_intergration_v5.** { *; }

# Behåll Retrofit interfaces
-keep interface se.gritacademy.maryam.rasouli.malmo.api_intergration_v5.** { *; }
