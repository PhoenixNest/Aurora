package com.dev.aurora.fragment;

import android.os.Bundle;
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
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
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

    private boolean followTouch;

    private CoffeeMainFragmentBinding binding;
    private CoffeeViewModel mViewModel;

    private AMap aMap;
    private MyLocationStyle locationStyle;
    private AMapLocationClientOption clientOption;
    private UiSettings uiSettings;
    private AMapLocationClient locationClient;
    private OnLocationChangedListener locationChangedListener;

    private String cityCode;

    private RVMainPoiAdapter poiAdapter;
    private LatLng latLng;
    private BottomSheetBehavior<ViewGroup> behavior;
    private LatLng searchLatLng;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Coffee-onCreateView");
        followTouch = true;
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_main_fragment, container, false);
        binding.setLifecycleOwner(requireActivity());
        binding.mapView.onCreate(savedInstanceState);

        initTopView();
        initBottomView();
        initEvent();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("Coffee-onActivityCreated");
        mViewModel = new ViewModelProvider(requireActivity()).get(CoffeeViewModel.class);
        locationStyle = mViewModel.getLocationStyle().getValue();
        clientOption = mViewModel.getClientOption().getValue();
    }

    /**
     * init Map
     * received SearchData(if any)
     */
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Coffee-onResume");
        SysUtils.closeKeyBoard(requireActivity().getApplicationContext(), getView());
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

        getSearchData();
    }

    // Map
    private void initMap() {
        uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(false);//指南针
        uiSettings.setIndoorSwitchEnabled(true);//室内地图
        uiSettings.setGestureScaleByMapCenter(true);//中心缩放
        uiSettings.setAllGesturesEnabled(true);//手势
        uiSettings.setZoomControlsEnabled(false);//缩放
        uiSettings.setScaleControlsEnabled(true);//默认比例尺
        uiSettings.setMyLocationButtonEnabled(false);//定位按钮
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//Logo

        aMap.setTrafficEnabled(false);//路况
        aMap.showMapText(true);//文字标示
        aMap.showBuildings(true);//3D建筑
        aMap.showIndoorMap(true);//室内地图
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//模式

        aMap.setMyLocationStyle(locationStyle);
        aMap.setOnMapTouchListener(this);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    // TopView
    private void initTopView() {
        int statusBarHeight = SysUtils.getStatusHeight(requireActivity());
        binding.includeTop.coffeeTop.setPadding(0, statusBarHeight, 45, 0);
        binding.includeTop.coffeeTop.getLayoutParams().height = SysUtils.dp2px(56) + statusBarHeight;
        binding.includeTop.coffeeTop.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));
    }

    // BottomView
    private void initBottomView() {
        binding.includeBottom.tvBSStatus.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));

        behavior = BottomSheetBehavior.from(binding.includeBottom.consCoffeeBS);
        behavior.setHideable(false);
        behavior.setPeekHeight(SysUtils.dp2px(56), true);
        int bottomMaxHeight = SysUtils.getWindowHeight(requireActivity()) - SysUtils.getStatusHeight(requireActivity());
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
        mViewModel.getPoiLiveData(requireActivity(), 0, poiType, cityCode, new LatLonPoint(latLng.latitude, latLng.longitude))
                .observe(this, poiItems -> {
                    poiAdapter.submitList(poiItems);
                    poiAdapter.setListener(position -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ConstUtils.KEY_poiItem, poiAdapter.getPoiItemList().get(position));
                        Navigation.findNavController(binding.includeBottom.rvBSCoffee)
                                .navigate(R.id.action_coffeeFragment_to_coffeeDetailsFragment, bundle);
                    });
                });
    }

    // SearchData
    private void getSearchData() {
        mViewModel.getTipLiveData().observe(requireActivity(), tip -> {
            if (tip != null) {
                followTouch = false;
                searchLatLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(searchLatLng, 16f, 45, 0)));
                Marker marker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(SysUtils.vector2Bitmap(requireContext(), R.drawable.ic_marker_24dp)))
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbGPS:
                followTouch = true;
                if (aMap.getMapScreenMarkers().size() > 1) aMap.clear();
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 45, 0)));
                break;

            case R.id.fbSearch:
                Bundle bundle = new Bundle();
                bundle.putString(ConstUtils.KEY_cityCode, cityCode);
                Navigation.findNavController(v).navigate(R.id.action_coffeeFragment_to_searchFragment, bundle);
                break;

            case R.id.fbFoods:
                getPoiList(ConstUtils.poiType_Food);
                break;

            case R.id.fbDrink:
                getPoiList(ConstUtils.poiType_Drinks);
                break;

            case R.id.fbHotel:
                getPoiList(ConstUtils.poiType_Hotel);
                break;

            case R.id.fbViews:
                getPoiList(ConstUtils.poiType_Views);
                break;

            case R.id.fbShopping:
                getPoiList(ConstUtils.poiType_Shopping);
                break;

            case R.id.fbSport:
                getPoiList(ConstUtils.poiType_Sport);
                break;

            case R.id.fbHealth:
                getPoiList(ConstUtils.poiType_Health);
                break;

            case R.id.fbTraffic:
                getPoiList(ConstUtils.poiType_Traffic);
                break;

        }
    }

    // open GPS
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        locationChangedListener = onLocationChangedListener;
        locationClient = new AMapLocationClient(requireActivity());
        mViewModel.getAMapLocation(locationClient).observe(requireActivity(), aMapLocation -> {
            if (aMapLocation.getErrorCode() == 0) {
                if (followTouch) {
                    System.out.println("Coffee-Success : " + aMapLocation.toString());
                    locationChangedListener.onLocationChanged(aMapLocation);
                    cityCode = aMapLocation.getCityCode();

                    latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 18f, 45, 0)));

                    poiAdapter.setStartLatLng(latLng);
                }
            } else {
                System.out.println("Coffee-ErrorCode : " + aMapLocation.getErrorCode());
                MsgUtils.showToast(requireActivity(), "无法获取当前位置");
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
        System.out.println("Coffee-onSaveInstanceState");
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Coffee-OnPause");
        binding.mapView.onPause();
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        deactivate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Coffee-onDestroyView");
        binding.mapView.onDestroy();
        mViewModel.setTipData(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Coffee-onDestroy");
        binding.mapView.onDestroy();
        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

}