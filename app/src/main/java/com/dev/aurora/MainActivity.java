package com.dev.aurora;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dev.aurora.utils.SysUtils;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = new String[]{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysUtils.enableImmersiveMode(this);
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
    }

}