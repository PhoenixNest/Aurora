package com.dev.aurora.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.dev.aurora.R;
import com.dev.aurora.adapter.RVMainPoiAdapter;
import com.dev.aurora.databinding.CoffeeMainFragmentBinding;
import com.dev.aurora.utils.AnimationUtils;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.MsgUtils;
import com.dev.aurora.utils.SysUtils;
import com.dev.aurora.viewmodel.CoffeeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CoffeeMainFragment extends Fragment implements View.OnClickListener, LocationSource,
        AMap.OnMapTouchListener {

    private boolean isAlreadyGetNavData = false;
    private boolean followTouch;

    private CoffeeMainFragmentBinding binding;

    private CoffeeViewModel mViewModel;

    private AMap aMap;
    private MyLocationStyle locationStyle;
    private AMapLocationClientOption clientOption;
    private AMapLocationClient locationClient;
    private OnLocationChangedListener locationChangedListener;

    private String cityCode;

    private RVMainPoiAdapter poiAdapter;
    private LatLng latLng;
    private BottomSheetBehavior<ViewGroup> behavior;
    private LatLng searchLatLng;
    private SharedPreferences sharedPreferences;
    private String city;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ConstUtils.coffeeMainFragmentName, "---onCreateView---");

        followTouch = true;
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_main_fragment, container, false);
        binding.setLifecycleOwner(requireActivity());
        binding.mapView.onCreate(savedInstanceState);

        initTopViewUi();
        initBottomViewUi();
        initNavDrawerData();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(ConstUtils.coffeeMainFragmentName, "---onActivityCreated---");

        mViewModel = new ViewModelProvider(requireActivity()).get(CoffeeViewModel.class);
        locationStyle = mViewModel.getLocationStyle().getValue();
        clientOption = mViewModel.getClientOption().getValue();
    }

    /**
     * init Map
     * received SearchData(which comes from the CoffeeSearchFragment)
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(ConstUtils.coffeeMainFragmentName, "---onResume---");

        SysUtils.getInstance().closeKeyBoard(requireActivity().getApplicationContext(), getView());
        binding.mapView.onResume();

        // Setup Map Data
        if (aMap == null) {
            aMap = binding.mapView.getMap();
            initMap();
        } else {
            aMap.removecache();
            aMap = binding.mapView.getMap();
            initMap();
        }

        initEvent();
        getSearchData();
    }

    // Map
    private void initMap() {
        sharedPreferences = requireActivity().getSharedPreferences(ConstUtils.app_sp_name, Context.MODE_PRIVATE);
        boolean isOpenCompass = sharedPreferences.getBoolean(ConstUtils.spKEY_isOpenCompass, false);
        boolean isOpenTraffic = sharedPreferences.getBoolean(ConstUtils.spKEY_isOpenTraffic, false);
        int mapMode = sharedPreferences.getInt(ConstUtils.spKEY_currentMapMode, AMap.MAP_TYPE_NORMAL);

        Log.d(ConstUtils.coffeeMainFragmentName, ConstUtils.spKEY_isOpenCompass + ": " + isOpenCompass);
        Log.d(ConstUtils.coffeeMainFragmentName, ConstUtils.spKEY_isOpenTraffic + ": " + isOpenTraffic);
        Log.d(ConstUtils.coffeeMainFragmentName, ConstUtils.spKEY_currentMapMode + ": " + mapMode);

        aMap.getUiSettings().setCompassEnabled(isOpenCompass);//指南针
        aMap.getUiSettings().setIndoorSwitchEnabled(true);//室内地图
        aMap.getUiSettings().setGestureScaleByMapCenter(true);//中心缩放
        aMap.getUiSettings().setAllGesturesEnabled(true);//手势
        aMap.getUiSettings().setZoomControlsEnabled(false);//缩放
        aMap.getUiSettings().setScaleControlsEnabled(true);//默认比例尺
        aMap.getUiSettings().setMyLocationButtonEnabled(false);//定位按钮
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//Logo

        aMap.setMapTextZIndex(6);
        aMap.setTrafficEnabled(isOpenTraffic);//路况
        aMap.showMapText(true);//文字标示
        aMap.showBuildings(true);//3D建筑
        aMap.showIndoorMap(true);//室内地图
        aMap.setMapType(mapMode);//模式

        // 自定义地图
        aMap.setCustomMapStyle(new CustomMapStyleOptions()
                .setEnable(true)
                .setStyleDataPath("app/assets/style.data")
                .setStyleExtraPath("app/assets/style_extra.data"));

        aMap.setMyLocationStyle(locationStyle);
        aMap.setOnMapTouchListener(this);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    // NavDrawer Weather Data
    private void initNavDrawerData() {
        if (!TextUtils.isEmpty(city)) {
            binding.setCity(city);
            mViewModel.getUpdateLiveData().observe(getViewLifecycleOwner(), update -> {
                if (update != null) binding.setUpdateData(update);
            });
            mViewModel.getNowWeatherLiveData().observe(getViewLifecycleOwner(), nowBase -> {
                if (nowBase != null) binding.setWeatherData(nowBase);
            });
        }
    }

    // TopView
    private void initTopViewUi() {
        int statusBarHeight = SysUtils.getInstance().getStatusHeight(requireActivity());
        binding.includeTop.coffeeTop.setPadding(0, statusBarHeight, 45, 0);
        binding.includeTop.coffeeTop.getLayoutParams().height = SysUtils.getInstance().dp2px(56) + statusBarHeight;
        binding.includeTop.coffeeTop.setBackgroundColor(requireActivity().getColor(R.color.colorPrimaryDark));
    }

    // BottomView
    private void initBottomViewUi() {
        binding.includeBottom.tvBSStatus.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));

        behavior = BottomSheetBehavior.from(binding.includeBottom.consCoffeeBS);
        behavior.setHideable(false);
        behavior.setPeekHeight(SysUtils.getInstance().dp2px(56), true);
        int bottomMaxHeight = SysUtils.getInstance().getWindowHeight(requireActivity()) - SysUtils.getInstance().getStatusHeight(requireActivity());
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != BottomSheetBehavior.STATE_EXPANDED) {
                    ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                    if (bottomSheet.getHeight() > bottomMaxHeight) {
                        layoutParams.height = bottomMaxHeight;
                        bottomSheet.setLayoutParams(layoutParams);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeBottom.rvBSCoffee.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        binding.includeBottom.rvBSCoffee.addItemDecoration(itemDecoration);

        poiAdapter = new RVMainPoiAdapter();
        binding.includeBottom.rvBSCoffee.setAdapter(poiAdapter);
    }

    // BottomView -> PoiData
    private void getPoiList(String poiType) {
        if (latLng == null) {
            Log.d(ConstUtils.coffeeMainFragmentName, "无法获得当前位置，请检查定位功能是否开启");
            MsgUtils.getInstance().showToast(requireContext(), getString(R.string.mapErrorMsg));
        } else {
            mViewModel.getPoiData(requireContext(), poiType, cityCode, new LatLonPoint(latLng.latitude, latLng.longitude))
                    .observe(getViewLifecycleOwner(), poiItems -> {
                        Log.d(ConstUtils.coffeeMainFragmentName, "获得的Poi数据为：" + poiItems.toString());

                        poiAdapter.submitList(poiItems);
                    });

            poiAdapter.setListener(position -> {
                Log.d(ConstUtils.coffeeMainFragmentName, "转接的Poi数据为：" + poiAdapter.getPoiItemList().get(position).toString());

                Bundle bundle = new Bundle();
                bundle.putParcelable(ConstUtils.KEY_poiItem, poiAdapter.getPoiItemList().get(position));
                Navigation.findNavController(binding.includeBottom.rvBSCoffee)
                        .navigate(R.id.action_coffeeFragment_to_coffeeDetailsFragment, bundle);
            });
        }
    }

    // SearchData
    private void getSearchData() {
        mViewModel.getTipLiveData().observe(requireActivity(), tip -> {
            if (tip != null) {
                Log.d(ConstUtils.coffeeMainFragmentName, "接受到的Tip数据为：" + tip.toString());

                followTouch = false;
                searchLatLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(searchLatLng, 17f, 30, 0)));
                Marker marker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(SysUtils.getInstance().vector2Bitmap(requireContext(), R.drawable.ic_marker_24dp)))
                        .position(searchLatLng));
                marker.setAnimation(AnimationUtils.getAMapMarkerAnimation());
                marker.startAnimation();
            }

        });

    }

    // Listener
    private void initEvent() {
        binding.fbGPS.setOnClickListener(this);
        binding.fbSearch.setOnClickListener(this);

        binding.ivMapUp.setOnClickListener(this);
        binding.ivMapDown.setOnClickListener(this);

        binding.includeTop.ivWeather.setOnClickListener(this);
        binding.includeTop.ivSetting.setOnClickListener(this);

        binding.includeBottom.tvBSStatus.setOnClickListener(this);
        binding.includeBottom.fbFoods.setOnClickListener(this);
        binding.includeBottom.fbDrink.setOnClickListener(this);
        binding.includeBottom.fbHotel.setOnClickListener(this);
        binding.includeBottom.fbViews.setOnClickListener(this);
        binding.includeBottom.fbShopping.setOnClickListener(this);
        binding.includeBottom.fbSport.setOnClickListener(this);
        binding.includeBottom.fbHealth.setOnClickListener(this);
        binding.includeBottom.fbTraffic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbGPS:
                followTouch = true;
                if (aMap.getMapScreenMarkers().size() > 1) aMap.clear();
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 17f, 30, 0)));
                break;

            case R.id.fbSearch:
                Bundle bundle = new Bundle();
                bundle.putString(ConstUtils.KEY_cityCode, cityCode);
                Navigation.findNavController(view).navigate(R.id.action_coffeeFragment_to_searchFragment, bundle);
                break;

            case R.id.ivMapUp:
                followTouch = false;
                aMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;

            case R.id.ivMapDown:
                followTouch = false;
                aMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;

            case R.id.ivWeather:
                binding.dlCoffee.openDrawer(binding.includeNavCoffee.getRoot());
                break;

            case R.id.ivSetting:
                Navigation.findNavController(view).navigate(R.id.action_coffeeFragment_to_coffeeSettingFragment);
                break;

            case R.id.tvBSStatus:
                binding.includeBottom.nsvCoffeeBottom.fullScroll(View.FOCUS_UP);
                break;

            case R.id.fbDrink:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Drinks);
                break;

            case R.id.fbFoods:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Food);
                break;

            case R.id.fbHotel:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Hotel);
                break;

            case R.id.fbViews:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Views);
                break;

            case R.id.fbShopping:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Shopping);
                break;

            case R.id.fbSport:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Sport);
                break;

            case R.id.fbHealth:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Health);
                break;

            case R.id.fbTraffic:
                binding.includeBottom.ivBSNothing.setVisibility(View.GONE);
                mViewModel.invalidateDataSource();
                getPoiList(ConstUtils.poiType_Traffic);
                break;

        }
    }

    // open GPS
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener = onLocationChangedListener;
        locationClient = new AMapLocationClient(requireActivity());
        mViewModel.getAMapLocation(locationClient).observe(getViewLifecycleOwner(), aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                if (followTouch) {
                    Log.d(ConstUtils.coffeeMainFragmentName, "GPS定位成功：" + aMapLocation.toString());

                    locationChangedListener.onLocationChanged(aMapLocation);
                    city = aMapLocation.getCity();
                    cityCode = aMapLocation.getCityCode();

                    // 获取NavDrawer随机背景图url && 天气信息
                    if (!isAlreadyGetNavData) {
                        // Image
                        // mViewModel.getPixabayDataByVolley(requireContext());

                        // Weather
                        mViewModel.getWeatherData(requireContext(), city);
                        initNavDrawerData();
                        isAlreadyGetNavData = true;
                    }

                    Log.d(ConstUtils.coffeeMainFragmentName, "当前城市：" + city);
                    Log.d(ConstUtils.coffeeMainFragmentName, "当前城市码：" + cityCode);

                    latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 17f, 30, 0)));

                    poiAdapter.setStartLatLng(latLng);
                }
            } else {
                Log.d(ConstUtils.coffeeMainFragmentName, "GPS定位失败，错误码为：" + aMapLocation.getErrorCode());

                MsgUtils.getInstance().showToast(requireContext(), getString(R.string.mapErrorMsg));
            }
        });
        locationClient.setLocationOption(clientOption);
        locationClient.startLocation();

    }

    // close GPS
    @Override
    public void deactivate() {
        locationChangedListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }

        locationClient = null;
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        followTouch = false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ConstUtils.coffeeMainFragmentName, "---onSaveInstanceState---");

        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(ConstUtils.coffeeMainFragmentName, "OnPause");

        binding.mapView.onPause();
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        deactivate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ConstUtils.coffeeMainFragmentName, "---onDestroyView---");

        mViewModel.setTipData(null);
        binding.mapView.onDestroy();
        sharedPreferences = null;
        poiAdapter = null;
        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel = null;
        Log.d(ConstUtils.coffeeMainFragmentName, "onDestroy");
    }

}