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

# 排除okhttp
  -dontwarn com.squareup.**
  -dontwarn okio.**
  -keep public class org.codehaus.* { *; }
  -keep public class java.nio.* { *; }

# 排除HeWeather
  -dontwarn interfaces.heweather.com.interfacesmodule.**
  -keep class interfaces.heweather.com.interfacesmodule.** { *;}

#3D 地图 V5.0.0之后：
  -keep   class com.amap.api.maps.**{*;}
  -keep   class com.autonavi.**{*;}
  -keep   class com.amap.api.trace.**{*;}

#定位
  -keep class com.amap.api.location.**{*;}
  -keep class com.amap.api.fence.**{*;}
  -keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
  -keep   class com.amap.api.services.**{*;}

#导航
  -keep class com.amap.api.navi.**{*;}
  -keep class com.autonavi.**{*;}