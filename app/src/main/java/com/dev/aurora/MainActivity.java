package com.dev.aurora;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysUtils.getInstance().enableImmersiveMode(this);
        HeConfig.init(ConstUtils.HeWeatherDevID, ConstUtils.HeWeatherKey);
        HeConfig.switchToFreeServerNode();
    }
}