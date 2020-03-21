package com.dev.aurora.viewmodel;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.android.volley.toolbox.StringRequest;
import com.dev.aurora.data.PixabayBean;
import com.dev.aurora.data.PoiItemDataSourceFactory;
import com.dev.aurora.db.SearchItem;
import com.dev.aurora.repository.CoffeeRepository;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.VolleyUtils;
import com.google.gson.Gson;

import java.util.List;
import java.util.Random;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.basic.Update;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class CoffeeViewModel extends AndroidViewModel {
    private final static String KEY_LocationStyle = "KEY_LocationStyle";
    private final static String KEY_ClientOption = "KEY_ClientOption";

    private SavedStateHandle handle;
    private MutableLiveData<AMapLocation> aMapLocationLiveData;
    private MutableLiveData<Tip> tipLiveData;
    private MutableLiveData<Update> updateLiveData;
    private MutableLiveData<NowBase> nowWeatherLiveData;
    private MutableLiveData<PixabayBean.HitsBean> pixabayLiveData;

    private CoffeeRepository coffeeRepository;
    private DataSource<Integer, PoiItem> dataSource;

    private boolean isGetWeatherDataSuccess;

    // 初始化MainFragment中蓝点样式 & 地图客户端配置
    public CoffeeViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        coffeeRepository = new CoffeeRepository(application);

        if (!handle.contains(KEY_LocationStyle) && !handle.contains(KEY_ClientOption)) {
            MyLocationStyle mLocationStyle = new MyLocationStyle();
            mLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//定位模式
            mLocationStyle.strokeWidth(0);//定位蓝点外圈边框大小
            mLocationStyle.strokeColor(Color.TRANSPARENT);//定位蓝点外圈边框颜色
            mLocationStyle.radiusFillColor(Color.parseColor("#4064b5f6"));//定位蓝点外圈颜色
            mLocationStyle.showMyLocation(true);

            AMapLocationClientOption mLocationClientOption = new AMapLocationClientOption();
            mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//定位模式 高精度
            mLocationClientOption.setNeedAddress(true);//返回地址信息
            mLocationClientOption.setWifiScan(true);//强制刷新Wifi
            mLocationClientOption.setMockEnable(false);//允许模拟位置
            mLocationClientOption.setOnceLocation(false);//单次定位
            mLocationClientOption.setInterval(2000);//定位间隔
            mLocationClientOption.setLocationCacheEnable(false);//定位缓存

            handle.set(KEY_LocationStyle, mLocationStyle);
            handle.set(KEY_ClientOption, mLocationClientOption);
        }

        this.handle = handle;
    }

    // 获取MainFragment地图蓝点样式
    public LiveData<MyLocationStyle> getLocationStyle() {
        return handle.getLiveData(KEY_LocationStyle);
    }

    // 获取MainFragment定位客户端配置
    public LiveData<AMapLocationClientOption> getClientOption() {
        return handle.getLiveData(KEY_ClientOption);
    }

    // 获取MainFragment最后一次定位结果数据
    public LiveData<AMapLocation> getAMapLocation(AMapLocationClient client) {
        if (aMapLocationLiveData == null) {
            aMapLocationLiveData = new MediatorLiveData<>();
        }

        client.setLocationListener(aMapLocation -> aMapLocationLiveData.setValue(aMapLocation));

        Log.d(ConstUtils.coffeeMainFragmentName, "最后一次定位数据为：" + aMapLocationLiveData.getValue());
        return aMapLocationLiveData;
    }

    // 获取NavDrawer中的实况天气数据
    public boolean getWeatherData(Context context, String location) {
        if (nowWeatherLiveData == null) {
            nowWeatherLiveData = new MutableLiveData<>();
            nowWeatherLiveData.setValue(null);
        }

        if (updateLiveData == null) {
            updateLiveData = new MutableLiveData<>();
            updateLiveData.setValue(null);
        }

        HeWeather.getWeatherNow(context, location, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.d(ConstUtils.coffeeMainFragmentName, "获取天气数据失败，错误原因为：" + throwable);

                isGetWeatherDataSuccess = false;
            }

            @Override
            public void onSuccess(Now now) {
                String json = new Gson().toJson(now);
                Log.d(ConstUtils.coffeeMainFragmentName, "获得的天气Json数据为：" + json);

                if (Code.OK.getCode().equalsIgnoreCase(now.getStatus())) {
                    updateLiveData.setValue(now.getUpdate());
                    nowWeatherLiveData.setValue(now.getNow());

                    Log.d(ConstUtils.coffeeMainFragmentName, "获取天气数据成功");

                    isGetWeatherDataSuccess = true;
                } else {
                    String status = now.getStatus();
                    Code errorCode = Code.toEnum(status);
                    Log.d(ConstUtils.coffeeMainFragmentName, "获取天气数据失败，错误码为：" + errorCode);

                    isGetWeatherDataSuccess = false;
                }
            }
        });

        return isGetWeatherDataSuccess;
    }

    // 返回天气数据更新时间
    public LiveData<Update> getUpdateLiveData() {
        return updateLiveData;
    }

    // 返回天气数据
    public LiveData<NowBase> getNowWeatherLiveData() {
        return nowWeatherLiveData;
    }

    public LiveData<PixabayBean.HitsBean> getPixabayDataByVolley(Context context) {
        if (pixabayLiveData == null) {
            pixabayLiveData = new MutableLiveData<>();
            pixabayLiveData.setValue(null);
        }
        String requestUrl = "https://pixabay.com/api/?key=15455499-22cfacf3fc2820d332ff649aa&image_type=photo&orientation=vertical&editors_choice=true&order=latest";
        StringRequest request = new StringRequest(requestUrl, response -> {
            int random = new Random().nextInt(19);
            PixabayBean.HitsBean hitsBean = new Gson().fromJson(requestUrl, PixabayBean.class).getHits().get(random);

            pixabayLiveData.setValue(hitsBean);
            Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片成功" + hitsBean.getPageURL());

        }, error -> Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片失败：" + error.toString()));

        VolleyUtils.getInstance(context).add(request);

        return pixabayLiveData;
    }

    /*public LiveData<PixabayBean> _getPixabayDataByRetrofit() {
        if (pixabayLiveData == null) {
            pixabayLiveData = new MutableLiveData<>();
            pixabayLiveData.setValue(null);
        }

        Pixabay call = RetrofitUtils.getInstance(ConstUtils.baseURL).create(Pixabay.class);
        call.getPixabayData(ConstUtils.pixabayKey,
                ConstUtils.pixabayImageType,
                ConstUtils.pixabayImageOrientation,
                ConstUtils.pixabayEditorsChoice,
                ConstUtils.pixabayOrderBy).enqueue(new Callback<PixabayBean>() {
            @Override
            public void onResponse(Call<PixabayBean> call, Response<PixabayBean> response) {
                Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片成功" + response.body().toString());
                pixabayLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<PixabayBean> call, Throwable throwable) {
                Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片失败：" + throwable);
            }
        });

        return pixabayLiveData;
    }*/

    // 获取MainFragment中的Poi列表
    public LiveData<PagedList<PoiItem>> getPoiData(Context context, String poiType, String cityCode, LatLonPoint latLonPoint) {
        PoiItemDataSourceFactory dataSourceFactory = new PoiItemDataSourceFactory(context, poiType, cityCode, latLonPoint);
        dataSource = dataSourceFactory.create();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(ConstUtils.pagingReloadPageSize)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(ConstUtils.pagingInitPageSize)
                .build();

        return new LivePagedListBuilder<>(dataSourceFactory, config).build();
    }

    public void invalidateDataSource() {
        if (dataSource != null) dataSource.invalidate();
    }

    // 设置SearchFragment中的Tip(单次保存，HostFragment的View被回收时置空)
    public void setTipData(Tip tip) {
        tipLiveData.setValue(tip);
    }

    // 返回SearchFragment中搜索Tip建议
    public LiveData<Tip> getTipLiveData() {
        if (tipLiveData == null) {
            tipLiveData = new MutableLiveData<>();
            tipLiveData.setValue(null);
        }

        Log.d(ConstUtils.coffeeMainFragmentName, "获得的Tip建议数据为：" + tipLiveData.getValue());
        return tipLiveData;
    }

    // 返回SearchFragment中历史搜素记录
    public LiveData<List<SearchItem>> getLiveSearchItemList() {
        return coffeeRepository.getLiveSearchItemList();
    }

    // 插入SearchFragment只中搜索记录
    public void insertSearchItem(SearchItem... searchItems) {
        coffeeRepository.insertSearchItem(searchItems);
    }

    // 删除SearchFragment中单条历史搜索记录
    public void deleteSingleSearchItem(SearchItem... searchItems) {
        coffeeRepository.deleteSingleSearchItem(searchItems);
    }

    // 删除SearchFragment所有历史搜索记录
    public void deleteAllSearchItem() {
        coffeeRepository.deleteAllSearchItem();
    }
}
