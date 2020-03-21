package com.dev.aurora.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amap.api.services.core.PoiItem;
import com.bumptech.glide.Glide;
import com.dev.aurora.R;
import com.dev.aurora.databinding.CoffeeDetailsFragmentBinding;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;

public class CoffeeDetailsFragment extends Fragment implements View.OnClickListener {

    private final int alphaMaxOffset = SysUtils.getInstance().dp2px(150);

    private CoffeeDetailsFragmentBinding binding;
    private PoiItem poiItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ConstUtils.coffeeDetailFragmentName, "---onCreateView---");
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_details_fragment, container, false);
        binding.setLifecycleOwner(this);
        setHasOptionsMenu(true);
        initUi();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ConstUtils.coffeeDetailFragmentName, "---onResume---");

        if (getArguments() != null) {
            poiItem = getArguments().getParcelable(ConstUtils.KEY_poiItem);
            binding.setData(poiItem);
        }

        initData();
        iniEvent();
    }

    private void initUi() {
        int statusBarHeight = SysUtils.getInstance().getStatusHeight(requireActivity());
        binding.toolbarPoiDetails.setPadding(0, statusBarHeight, 0, 0);
        binding.toolbarPoiDetails.getLayoutParams().height = SysUtils.getInstance().dp2px(56) + statusBarHeight;
        binding.toolbarPoiDetails.getBackground().setAlpha(0);
        binding.appBarPoiDetails.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset > -alphaMaxOffset) {
                binding.toolbarPoiDetails.getBackground().setAlpha(255 * -verticalOffset / alphaMaxOffset);
            } else {
                binding.toolbarPoiDetails.getBackground().setAlpha(255);
            }
        });

        binding.constraintTop.setBackgroundColor(requireActivity().getColor(R.color.colorPrimaryDark));
    }

    private void initData() {
        if (poiItem.getPhotos().size() > 0) {
            Glide.with(binding.getRoot())
                    .load(poiItem.getPhotos().get(0).getUrl())
                    .placeholder(R.drawable.ic_loading_gray_72dp)
                    .error(R.drawable.ic_error_red_72dp)
                    .into(binding.ivPoiDetailsImage);
        } else {
            binding.ivPoiDetailsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            binding.ivPoiDetailsImage.setImageResource(R.drawable.ic_nothing_white_52dp);
        }

        if (!TextUtils.isEmpty(poiItem.getPoiExtension().getmRating())) {
            binding.rbPoiDetailsRank.setRating(Float.parseFloat(poiItem.getPoiExtension().getmRating()));
        }

        binding.tvPoiDetailsRank.setText(String.valueOf(binding.rbPoiDetailsRank.getRating()));
        binding.tvPoiDetailsAddress.setSelected(true);
        binding.tvPoiDetailsOpenTime.setSelected(true);
    }

    private void iniEvent() {
        binding.tvPoiDetailsTel.setOnClickListener(this);
        binding.tvPoiDetailsEmail.setOnClickListener(this);
        binding.tvPoiDetailsWebStte.setOnClickListener(this);
        binding.fbPoiDetailsGo.setOnClickListener(this);
        binding.toolbarPoiDetails.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbPoiDetailsGo:
                Navigation.findNavController(view).navigateUp();
                break;

            case R.id.tvPoiDetailsTel:
                Intent intentCall = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + poiItem.getTel());
                intentCall.setData(data);

                new AlertDialog.Builder(requireContext())
                        .setTitle("将拨打 : " + poiItem.getTel())
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .setPositiveButton("确定", (dialog, which) -> startActivity(intentCall))
                        .show();

                break;

            case R.id.tvPoiDetailsEmail:

                break;

            case R.id.tvPoiDetailsWebStte:

                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ConstUtils.coffeeDetailFragmentName, "---onDestroyView---");

        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ConstUtils.coffeeDetailFragmentName, "---onDestroy---");
    }
}
