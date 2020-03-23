package com.dev.aurora.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.LatLonPoint;
import com.bumptech.glide.Glide;
import com.dev.aurora.R;
import com.dev.aurora.adapter.RVMainPoiAdapter;
import com.dev.aurora.databinding.CoffeeMainFragmentBinding;
import com.dev.aurora.utils.AnimationUtils;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.MsgUtils;
import com.dev.aurora.utils.SysUtils;
import com.dev.aurora.viewmodel.CoffeeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CoffeeMainFragment extends Fragment implements View.OnClickListener, LocationSource,
        AMap.OnMapTouchListener {

    private boolean isAlreadyGetNavData = false;
    private boolean followTouch;

    private CoffeeMainFragmentBinding binding;
    private CoffeeViewModel mViewModel;
    private SharedPreferences sharedPreferences;

    private AMap aMap;
    private MyLocationStyle locationStyle;
    private CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();
    private AMapLocationClientOption clientOption;
    private AMapLocationClient locationClient;
    private OnLocationChangedListener locationChangedListener;

    private RVMainPoiAdapter poiAdapter;
    private String imageURL;

    private String city;
    private String cityCode;
    private String address;
    private LatLng currentLatLng;
    private LatLng searchLatLng;

    private BottomSheetBehavior<ViewGroup> bottomBehavior;
    private BottomSheetBehavior<CoordinatorLayout> bottomBehaviorNav;
    private BottomSheetBehavior<CoordinatorLayout> bottomBehaviorNavRoute;
    private Poi endPoi;
    private LatLng poiItemLatLng;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ConstUtils.coffeeMainFragmentName, "---onCreateView---");

        followTouch = true;
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_main_fragment, container, false);
        binding.setLifecycleOwner(requireActivity());
        binding.mapView.onCreate(savedInstanceState);

        initTopViewUi();
        initBottomViewUi();
        initNavDrawerWeatherData();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(ConstUtils.coffeeMainFragmentName, "---onActivityCreated---");

        mViewModel = new ViewModelProvider(requireActivity()).get(CoffeeViewModel.class);
        locationStyle = mViewModel.getLocationStyle().getValue();
        clientOption = mViewModel.getClientOption().getValue();

        initBottomNavViewUi();
        initBottomNavRouteViewUi();
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
        getPoiItemData();
        initNavDrawerImageData();
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

        // 更换当前地图为地图
        setMapCustomStyleFile(requireContext());
        mapStyleOptions.setEnable(true);
        aMap.setCustomMapStyle(mapStyleOptions);

        aMap.setMyLocationStyle(locationStyle);
        aMap.setOnMapTouchListener(this);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    // 自定义地图
    private void setMapCustomStyleFile(Context context) {
        InputStream inputStyleStream = null;
        InputStream inputExtraStyleStream = null;
        try {
            inputStyleStream = context.getAssets().open("style.data");
            inputExtraStyleStream = context.getAssets().open("style_extra.data");
            byte[] dataByte = new byte[inputStyleStream.available()];
            byte[] extraData = new byte[inputExtraStyleStream.available()];
            inputStyleStream.read(dataByte);
            inputExtraStyleStream.read(extraData);

            // 设置自定义样式
            if (mapStyleOptions != null) {
                mapStyleOptions.setStyleData(dataByte);
                mapStyleOptions.setStyleExtraData(extraData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStyleStream != null && inputExtraStyleStream != null) {
                    inputStyleStream.close();
                    inputExtraStyleStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // NavDrawer Header Data(Random)
    private void initNavDrawerImageData() {
        mViewModel.getPixabayDataByVolley(requireContext()).observe(requireActivity(), hitsBean -> {
            if (hitsBean != null) {
                imageURL = hitsBean.getWebformatURL();
                binding.setHeaderData(hitsBean);

                Glide.with(requireContext())
                        .load(imageURL)
                        .timeout(10 * 1000)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.includeNavCoffee.ivNavHeader);
            }
        });
    }

    // NavDrawer Weather Data
    private void initNavDrawerWeatherData() {
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

        bottomBehavior = BottomSheetBehavior.from(binding.includeBottom.consCoffeeBS);
        bottomBehavior.setHideable(false);
        bottomBehavior.setPeekHeight(SysUtils.getInstance().dp2px(56), true);
        int bottomMaxHeight = SysUtils.getInstance().getWindowHeight(requireActivity()) - SysUtils.getInstance().getStatusHeight(requireActivity());
        bottomBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.includeBottom.rvBSCoffee.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        binding.includeBottom.rvBSCoffee.addItemDecoration(itemDecoration);

        poiAdapter = new RVMainPoiAdapter();
        binding.includeBottom.rvBSCoffee.setAdapter(poiAdapter);
    }

    // BottomView -> getPoiData
    private void getPoiList(String poiType) {
        if (currentLatLng == null) {
            Log.d(ConstUtils.coffeeMainFragmentName, "无法获得当前位置，请检查定位功能是否开启");
            MsgUtils.getInstance().showToast(requireContext(), getString(R.string.mapErrorMsg));
        } else {
            mViewModel.getPoiData(requireContext(), poiType, cityCode, new LatLonPoint(currentLatLng.latitude, currentLatLng.longitude))
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

    // BottomNavView
    private void initBottomNavViewUi() {
        binding.includeBottomNav.consCoffeeBSNav.setBackgroundColor(Color.WHITE);
        binding.includeBottomNav.consBSNavStatus.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));

        binding.includeBottomNav.tvBSNavEnd.setSelected(true);
        binding.includeBottomNav.tvBSNavAddress.setSelected(true);

        bottomBehaviorNav = BottomSheetBehavior.from(binding.includeBottomNav.coorCoffeeBSNav);
        bottomBehaviorNav.setHideable(true);
        bottomBehaviorNav.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    // BottomNavRouteView
    private void initBottomNavRouteViewUi() {
        binding.includeBottomNavRoute.consCoffeeBSNavRoute.setBackgroundColor(Color.WHITE);
        binding.includeBottomNavRoute.consBSNavRouteStatus.setBackgroundColor(requireActivity().getColor(R.color.viewColor));

        binding.includeBottomNavRoute.tvBSNavRouteEnd.setSelected(true);
        binding.includeBottomNavRoute.tvBSNavRouteStart.setSelected(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.includeBottomNavRoute.rvBSNavRoute.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        binding.includeBottomNavRoute.rvBSNavRoute.addItemDecoration(dividerItemDecoration);

        bottomBehaviorNavRoute = BottomSheetBehavior.from(binding.includeBottomNavRoute.coorCoffeeBSNavRoute);
        bottomBehaviorNavRoute.setHideable(true);
        bottomBehaviorNavRoute.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    // 添加Nav 起点Marker
    private void addStartMarker(LatLng currentLatLng) {
        Marker marker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nav_start))
                .position(currentLatLng));
        marker.setAnimation(AnimationUtils.getInstance().getAMapMarkerAnimation());
        marker.startAnimation();
    }

    // 添加Nav 终点Marker
    private void addEndMarker(LatLng endLatLng) {
        Marker marker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_nav_end))
                .position(endLatLng));
        marker.setAnimation(AnimationUtils.getInstance().getAMapMarkerAnimation());
        marker.startAnimation();
    }

    private void addNormalMarker(LatLng latLng) {
        Marker marker = aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(SysUtils.getInstance().vector2Bitmap(requireContext(), R.drawable.ic_tip_marker_32dp)))
                .position(latLng));
        marker.setAnimation(AnimationUtils.getInstance().getAMapMarkerAnimation());
        marker.startAnimation();
    }

    // 绘制Nav 路线规划线条
    private void addPolyLine(List<LatLng> latLngList) {
        aMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(24)
                .setDottedLine(false));
    }

    /* BottomNavView getSearchData
     * SearchFragment -> MainFragment
     */
    @SuppressLint("SetTextI18n")
    private void getSearchData() {
        mViewModel.getTipLiveData().observe(requireActivity(), tip -> {
            if (tip != null) {
                Log.d(ConstUtils.coffeeMainFragmentName, "接受到的Tip数据为：" + tip.toString());

                // 隐藏顶部功能面板与原底部面板
                followTouch = false;
                hidePanel();

                // 展开Nav规划面板
                bottomBehaviorNav.setHideable(false);
                bottomBehaviorNav.setState(BottomSheetBehavior.STATE_EXPANDED);

                searchLatLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(searchLatLng, 17f, 30, 0)));

                // 添加Tip返回结果的地点Marker
                addNormalMarker(searchLatLng);

                endPoi = new Poi(tip.getName(), searchLatLng, "");
                binding.setTipData(tip);
                binding.setTipRouteData(tip);

                float navDistance = AMapUtils.calculateLineDistance(currentLatLng, searchLatLng);
                binding.includeBottomNav.tvBSNavDistance.setText(navDistance + " M");
            }
        });

    }

    /* BottomNavView getPoiItemData
     * (DetailsFragment -> MainFragment)
     */
    private void getPoiItemData() {
        mViewModel.getPoiItemLiveData().observe(getViewLifecycleOwner(), poiItem -> {
            if (poiItem != null) {
                // 隐藏顶部功能面板与原底部面板
                followTouch = false;
                hidePanel();

                // 展开Route选择面板
                bottomBehaviorNavRoute.setHideable(false);
                bottomBehaviorNavRoute.setState(BottomSheetBehavior.STATE_COLLAPSED);
                poiItemLatLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(poiItemLatLng, 17f, 30, 0)));

                // 添加PoiItem返回结果的地点Marker
                addNormalMarker(poiItemLatLng);

                endPoi = new Poi(poiItem.getAdName(), poiItemLatLng, "");
                binding.includeBottomNavRoute.tvBSNavRouteEnd.setText(poiItem.getAdName());
            }
        });
    }

    // 显示主面板
    private void showPanel() {
        AnimationUtils.getInstance().showAlphaAnimation(binding.includeTop.coffeeTop);
        AnimationUtils.getInstance().showAlphaAnimation(binding.fbGPS);
        AnimationUtils.getInstance().showAlphaAnimation(binding.fbSearch);
        AnimationUtils.getInstance().showAlphaAnimation(binding.includeBottom.consCoffeeBS);

        binding.includeTop.coffeeTop.setVisibility(View.VISIBLE);
        binding.fbGPS.setVisibility(View.VISIBLE);
        binding.fbSearch.setVisibility(View.VISIBLE);
        binding.includeBottom.consCoffeeBS.setVisibility(View.VISIBLE);
    }

    // 隐藏主面板
    private void hidePanel() {
        binding.includeTop.coffeeTop.setVisibility(View.INVISIBLE);
        binding.fbGPS.setVisibility(View.INVISIBLE);
        binding.fbSearch.setVisibility(View.INVISIBLE);
        binding.includeBottom.consCoffeeBS.setVisibility(View.INVISIBLE);
    }

    // Listener
    private void initEvent() {
        binding.fbGPS.setOnClickListener(this);
        binding.fbSearch.setOnClickListener(this);

        binding.ivMapUp.setOnClickListener(this);
        binding.ivMapDown.setOnClickListener(this);

        binding.includeNavCoffee.ivNavHeader.setOnClickListener(this);

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

        binding.includeBottomNav.btnBSNavClose.setOnClickListener(this);
        binding.includeBottomNav.btnBSNavRoute.setOnClickListener(this);

        binding.includeBottomNavRoute.btnBSNavRouteClose.setOnClickListener(this);
        binding.includeBottomNavRoute.btnBSNavRouteCar.setOnClickListener(this);
        binding.includeBottomNavRoute.btnBSNavRouteBus.setOnClickListener(this);
        binding.includeBottomNavRoute.btnBSNavRouteWalk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbGPS:
                followTouch = true;
                aMap.clear(true);
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLatLng, 17f, 30, 0)));
                break;

            case R.id.fbSearch:
                Bundle searchBundle = new Bundle();
                searchBundle.putString(ConstUtils.KEY_cityCode, cityCode);
                Navigation.findNavController(view).navigate(R.id.action_coffeeFragment_to_searchFragment, searchBundle);
                break;

            case R.id.btnBSNavClose:
                // 隐藏底部Nav面板
                bottomBehaviorNav.setHideable(true);
                bottomBehaviorNav.setState(BottomSheetBehavior.STATE_HIDDEN);
                showPanel();
                break;

            case R.id.btnBSNavRouteClose:
                // 隐藏底部Route面板
                bottomBehaviorNavRoute.setHideable(true);
                bottomBehaviorNavRoute.setState(BottomSheetBehavior.STATE_HIDDEN);

                // 监测是否从DetailsFragment返回
                if (poiItemLatLng != null) {
                    showPanel();
                    bottomBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;

            case R.id.btnBSNavRoute:
                // 展开Route面板 (Nav -> Route)
                bottomBehaviorNav.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomBehaviorNavRoute.setHideable(false);
                bottomBehaviorNavRoute.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.btnBSNavRouteWalk:
                AmapNaviPage.getInstance().showRouteActivity(
                        getContext(),
                        new AmapNaviParams(null, null, endPoi, AmapNaviType.WALK), callback);
                break;

            case R.id.btnBSNavRouteCar:
                AmapNaviPage.getInstance().showRouteActivity(
                        getContext(),
                        new AmapNaviParams(null, null, endPoi, AmapNaviType.DRIVER), callback);
                break;

            case R.id.btnBSNavRouteBus:
                mViewModel.getNavBusData(requireContext(), cityCode, currentLatLng, searchLatLng, ConstUtils.navMode_BUS);
                break;

            case R.id.ivNavHeader:
                Bundle imageNavBundle = new Bundle();
                imageNavBundle.putString(ConstUtils.KEY_imageURL, imageURL);
                Navigation.findNavController(view).navigate(R.id.action_coffeeMainFragment_to_coffeeNavHeaderFragment, imageNavBundle);
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
                    address = aMapLocation.getAddress();

                    // 获取NavDrawer天气信息(单次获取)
                    if (!isAlreadyGetNavData) {
                        // Weather
                        mViewModel.getWeatherData(requireContext(), city);
                        initNavDrawerWeatherData();
                        isAlreadyGetNavData = true;
                    }

                    Log.d(ConstUtils.coffeeMainFragmentName, "当前城市：" + city);
                    Log.d(ConstUtils.coffeeMainFragmentName, "当前城市码：" + cityCode);

                    currentLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(currentLatLng, 17f, 30, 0)));

                    poiAdapter.setStartLatLng(currentLatLng);
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
        Log.d(ConstUtils.coffeeMainFragmentName, "---OnPause---");

        binding.mapView.onPause();
        mViewModel.setTipData(null);
        mViewModel.setPoiItemData(null);
        mViewModel.clearBusPathLiveData();
        deactivate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ConstUtils.coffeeMainFragmentName, "---onDestroyView---");

        binding.mapView.onDestroy();
        bottomBehavior.setHideable(true);
        bottomBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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

    private INaviInfoCallback callback = new INaviInfoCallback() {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onArriveDestination(boolean b) {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onStopSpeaking() {

        }

        @Override
        public void onReCalculateRoute(int i) {

        }

        @Override
        public void onExitPage(int i) {

        }

        @Override
        public void onStrategyChanged(int i) {

        }

        @Override
        public View getCustomNaviBottomView() {
            return null;
        }

        @Override
        public View getCustomNaviView() {
            return null;
        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onMapTypeChanged(int i) {

        }

        @Override
        public View getCustomMiddleView() {
            return null;
        }

        @Override
        public void onNaviDirectionChanged(int i) {

        }

        @Override
        public void onDayAndNightModeChanged(int i) {

        }

        @Override
        public void onBroadcastModeChanged(int i) {

        }

        @Override
        public void onScaleAutoChanged(boolean b) {

        }
    };
}