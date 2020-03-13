package com.dev.aurora.viewmodel;

import android.content.Context;
import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

public class CoffeeViewModel extends ViewModel {
    private final static String KEY_LocationStyle = "KEY_LocationStyle";
    private final static String KEY_ClientOption = "KEY_ClientOption";

    private SavedStateHandle handle;
    private MutableLiveData<AMapLocation> aMapLocationLiveData;
    private MutableLiveData<List<PoiItem>> poiItemList;

    public CoffeeViewModel(SavedStateHandle handle) {
        if (!handle.contains(KEY_LocationStyle) && !handle.contains(KEY_ClientOption)) {
            MyLocationStyle mLocationStyle = new MyLocationStyle();
            mLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//定位模式
            mLocationStyle.strokeWidth(0);//定位蓝点外圈边框大小
            mLocationStyle.strokeColor(Color.TRANSPARENT);//定位蓝点外圈边框颜色
            mLocationStyle.radiusFillColor(Color.argb(50, 99, 214, 249));//定位蓝点外圈颜色
            mLocationStyle.showMyLocation(true);

            AMapLocationClientOption mLocationClientOption = new AMapLocationClientOption();
            mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//定位模式 高精度
            mLocationClientOption.setNeedAddress(true);//返回地址信息
            mLocationClientOption.setWifiScan(true);//强制刷新Wifi
            mLocationClientOption.setMockEnable(false);//允许模拟位置
            mLocationClientOption.setOnceLocation(false);//单次定位
            mLocationClientOption.setInterval(3000);//定位间隔

            handle.set(KEY_LocationStyle, mLocationStyle);
            handle.set(KEY_ClientOption, mLocationClientOption);
        }

        this.handle = handle;
    }

    public MutableLiveData<MyLocationStyle> getLocationStyle() {
        return handle.getLiveData(KEY_LocationStyle);
    }

    public MutableLiveData<AMapLocationClientOption> getClientOption() {
        return handle.getLiveData(KEY_ClientOption);
    }

    public LiveData<AMapLocation> getAMapLocation(AMapLocationClient client) {
        if (aMapLocationLiveData == null) {
            aMapLocationLiveData = new MediatorLiveData<>();
        }

        client.setLocationListener(aMapLocation -> aMapLocationLiveData.setValue(aMapLocation));
        return aMapLocationLiveData;
    }

    public LiveData<List<PoiItem>> getPoiLiveData(Context context,
                                                  int pageNum,
                                                  String poiType,
                                                  String cityCode,
                                                  LatLonPoint latLonPoint) {
        if (poiItemList == null) {
            poiItemList = new MediatorLiveData<>();
            poiItemList.setValue(null);
        }

        poiItemList.setValue(null);
        PoiSearch.Query query = new PoiSearch.Query(poiType, "", cityCode);
        query.setPageSize(15);
        query.setPageNum(pageNum);
        PoiSearch poiSearch = new PoiSearch(context, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 1500));
        poiSearch.searchPOIAsyn();
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                poiItemList.setValue(poiResult.getPois());
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });

        return poiItemList;
    }
}
