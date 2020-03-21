package com.dev.aurora.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dev.aurora.utils.ConstUtils;

public class PoiItemDataSource extends PageKeyedDataSource<Integer, PoiItem> {
    private Context context;
    private String poiType;
    private String cityCode;
    private LatLonPoint latLonPoint;

    PoiItemDataSource(Context context, String poiType, String cityCode, LatLonPoint latLonPoint) {
        this.context = context;
        this.poiType = poiType;
        this.cityCode = cityCode;
        this.latLonPoint = latLonPoint;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, PoiItem> callback) {
        PoiSearch.Query query = new PoiSearch.Query("", poiType, cityCode);
        query.setPageNum(1);
        query.setPageSize(ConstUtils.pagingReloadPageSize);
        query.setDistanceSort(true);
        query.setCityLimit(true);
        PoiSearch poiSearch = new PoiSearch(context, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 3000));
        poiSearch.searchPOIAsyn();
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                if (rCode == 1000) {
                    if (poiResult == null) {
                        Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据失败，返回数据为空");
                    } else {
                        Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据成功，数据为：" + poiResult.toString());
                        callback.onResult(poiResult.getPois(), null, 2);
                    }

                } else {
                    Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据失败，错误码为：" + rCode);
                }

            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });


    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PoiItem> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PoiItem> callback) {
        PoiSearch.Query query = new PoiSearch.Query("", poiType, cityCode);
        query.setPageNum(params.key);
        query.setPageSize(ConstUtils.pagingReloadPageSize);
        query.setDistanceSort(true);
        query.setCityLimit(true);
        PoiSearch poiSearch = new PoiSearch(context, query);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 2000));
        poiSearch.searchPOIAsyn();
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                if (rCode == 1000) {
                    if (poiResult == null) {
                        Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据失败，返回数据为空");
                    } else {
                        Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据成功，数据为：" + poiResult.toString());
                        callback.onResult(poiResult.getPois(), params.key + 1);
                    }
                } else {
                    Log.d(ConstUtils.coffeeMainFragmentName, "获取Poi数据失败，错误码为：" + rCode);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
    }
}
