# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\DownloadFiles\adt-bundle-windows-x86_64-20140321\adt-bundle-windows-x86_64-20140321\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-dontwarn org.joda.time.**
-keep class org.joda.time.** {
*;
}

-keep class com.king.mangaviewer.common.MangaPattern.** {
public *;
}

-keep class android.support.**{*;}
-keep class org.jsoup.**{*;}
-keep class com.google.**{*;}
-keep class me.grantland.widget.**{*;}