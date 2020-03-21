package com.dev.aurora.utils;

import android.Manifest;

import com.amap.api.maps.AMap;

public class ConstUtils {
    // HeWeather
    public static final String HeWeatherDevID = "your id";
    public static final String HeWeatherKey = "your key";

    // Pixabay
    public static final String baseURL = "https://pixabay.com/api/";
    public static final String pixabayKey = "your key";
    public static final String pixabayImageType = "photo";
    public static final String pixabayImageOrientation = "vertical";
    public static final String pixabayEditorsChoice = "true";
    public static final String pixabayOrderBy = "latest";


    public static final String app_sp_name = "AuroraSP";
    public static final String permission_name = "hasPermission";

    public static final String spKEY_isOpenCompass = "isOpenCompass";
    public static final String spKEY_isOpenTraffic = "isOpenTraffic";
    public static final String spKEY_currentMapMode = "currentMapMode";

    public static final String splashActivityName = "SplashActivity: ";
    public static final String mainActivityName = "MainActivity: ";
    public static final String coffeeMainFragmentName = "CoffeeMainFragment: ";
    public static final String coffeeSearchFragmentName = "CoffeeSearchFragment: ";
    public static final String coffeeDetailFragmentName = "CoffeeDetailFragment: ";
    public static final String coffeeSettingFragmentName = "CoffeeSettingFragment: ";

    public static final String dailyNewsFragmentName = "DailyNewsFragment: ";
    public static final String weatherFragmentName = "WeatherFragment: ";

    public static final String KEY_cityName = "CityName";
    public static final String KEY_cityCode = "CityCode";
    public static final String KEY_tipItem = "KEY_tipItem";
    public static final String KEY_poiItem = "KEY_poiItem";
    public static final String KEY_tipLatLonPoint = "KEY_tipLatLonPoint";

    public static final String poiType_Food = "050300";// 快餐厅
    public static final String poiType_Drinks = "050500";// 咖啡厅
    public static final String poiType_Hotel = "100100";// 宾馆酒店
    public static final String poiType_Views = "110000";// 风景名胜
    public static final String poiType_Shopping = "060400";// 超级商场
    public static final String poiType_Sport = "080100";// 运动场馆
    public static final String poiType_Health = "090100";// 综合医院
    public static final String poiType_Traffic = "150000";// 交通服务

    public static final int pagingInitPageSize = 30;
    public static final int pagingReloadPageSize = 15;

    public static final int[] MapMode = {
            AMap.MAP_TYPE_NORMAL,
            AMap.MAP_TYPE_NIGHT,
            AMap.MAP_TYPE_SATELLITE,
            AMap.MAP_TYPE_NAVI
    };

    public static final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,//用于进行网络定位
            Manifest.permission.ACCESS_FINE_LOCATION,//用于访问GPS定位
            Manifest.permission.ACCESS_NETWORK_STATE,//用于获取运营商信息，用于支持提供运营商信息相关的接口
            Manifest.permission.ACCESS_WIFI_STATE,//用于访问wifi网络信息，wifi信息会用于进行网络定位
            Manifest.permission.CHANGE_WIFI_STATE,//用于获取wifi的获取权限，wifi信息会用来进行网络定位
            Manifest.permission.INTERNET,//用于访问网络，网络定位需要上网
            Manifest.permission.READ_PHONE_STATE,//用于读取手机当前的状态
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//用于写入缓存数据到扩展存储卡
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,//用于申请调用A-GPS模块
            Manifest.permission.BLUETOOTH,//用于申请获取蓝牙信息进行室内定位
            Manifest.permission.BLUETOOTH_ADMIN,//用于申请获取蓝牙信息进行室内定位
            Manifest.permission.WAKE_LOCK//允许程序在手机屏幕关闭后后台进程仍然运行
    };
}
