package com.dev.aurora.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

public class PoiItemDataSourceFactory extends DataSource.Factory<Integer, PoiItem> {
    private Context context;
    private String poiType;
    private String cityCode;
    private LatLonPoint latLonPoint;

    public PoiItemDataSourceFactory(Context context, String poiType, String cityCode, LatLonPoint latLonPoint) {
        this.context = context;
        this.poiType = poiType;
        this.cityCode = cityCode;
        this.latLonPoint = latLonPoint;
    }

    @NonNull
    @Override
    public DataSource<Integer, PoiItem> create() {
        return new PoiItemDataSource(context, poiType, cityCode, latLonPoint);
    }
}
