package com.dev.aurora.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amap.api.maps.AMap;
import com.dev.aurora.R;
import com.dev.aurora.databinding.CoffeeSettingFragmentBinding;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;

public class CoffeeSettingFragment extends Fragment {

    private CoffeeSettingFragmentBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ConstUtils.coffeeSettingFragmentName, "---onCreateView---");

        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_setting_fragment, container, false);

        initUi();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ConstUtils.coffeeSettingFragmentName, "---onResume---");

        initData();
        initEvent();
    }

    private void initUi() {
        binding.toolbarSetting.setPadding(0, SysUtils.getInstance().getStatusHeight(requireActivity()), 0, 0);
    }

    private void initData() {
        sharedPreferences = requireActivity().getSharedPreferences(ConstUtils.app_sp_name, Context.MODE_PRIVATE);
        boolean isOpenCompass = sharedPreferences.getBoolean(ConstUtils.spKEY_isOpenCompass, false);
        boolean isOpenTraffic = sharedPreferences.getBoolean(ConstUtils.spKEY_isOpenTraffic, false);

        binding.switchSettingCompass.setChecked(isOpenCompass);
        binding.switchSettingLiveTraffic.setChecked(isOpenTraffic);
    }

    private void initEvent() {
        edit = sharedPreferences.edit();
        binding.toolbarSetting.setNavigationOnClickListener(view -> Navigation.findNavController(view).navigateUp());
        binding.switchSettingCompass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edit.putBoolean(ConstUtils.spKEY_isOpenCompass, true);
                edit.apply();
            } else {
                edit.putBoolean(ConstUtils.spKEY_isOpenCompass, false);
                edit.apply();
            }

        });

        binding.switchSettingLiveTraffic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edit.putBoolean(ConstUtils.spKEY_isOpenTraffic, true);
                edit.apply();
            } else {
                edit.putBoolean(ConstUtils.spKEY_isOpenTraffic, false);
                edit.apply();
            }

        });

        binding.rgMapMode.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    ((RadioButton) group.getChildAt(i)).setChecked(true);
                    edit = sharedPreferences.edit();
                    edit.putInt(ConstUtils.spKEY_currentMapMode, ConstUtils.MapMode[i]);
                    edit.apply();
                }
            }
        });

        RadioButton[] radioButtons = {binding.radioButton, binding.radioButton2, binding.radioButton3, binding.radioButton4};
        int mapMode = sharedPreferences.getInt(ConstUtils.spKEY_currentMapMode, AMap.MAP_TYPE_NORMAL);
        for (int i = 0; i < ConstUtils.MapMode.length; i++) {
            if (mapMode == ConstUtils.MapMode[i]) {
                radioButtons[i].setChecked(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ConstUtils.coffeeSettingFragmentName, "---onDestroyView---");

        sharedPreferences = null;
        edit = null;
        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

    @Override
    public void onDestroy() {
        Log.d(ConstUtils.coffeeSettingFragmentName, "---onDestroy---");
        super.onDestroy();
    }
}
