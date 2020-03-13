package com.dev.aurora.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.amap.api.services.core.PoiItem;
import com.bumptech.glide.Glide;
import com.dev.aurora.R;
import com.dev.aurora.databinding.CoffeeDetailsFragmentBinding;
import com.dev.aurora.utils.ConstUtils;

import org.jetbrains.annotations.NotNull;

public class CoffeeDetailsFragment extends Fragment implements View.OnClickListener {

    private CoffeeDetailsFragmentBinding binding;
    private PoiItem poiItem;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_details_fragment, container, false);
        binding.setLifecycleOwner(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            poiItem = getArguments().getParcelable(ConstUtils.KEY_poiItem);
            binding.setData(poiItem);
        }

        initData();
        iniEvent();
    }

    private void initUi() {

    }

    private void initData() {
        if (poiItem.getPhotos().size() > 0) {
            Glide.with(binding.getRoot())
                    .load(poiItem.getPhotos().get(0).getUrl())
                    .placeholder(R.drawable.ic_loading_gray_72dp)
                    .error(R.drawable.ic_error_red_72dp)
                    .into(binding.ivPoiDetailsImage);
        }

        if (!TextUtils.isEmpty(poiItem.getPoiExtension().getmRating())) {
            binding.rbPoiDetailsRank.setRating(Float.parseFloat(poiItem.getPoiExtension().getmRating()));
        }
        binding.tvPoiDetailsRank.setText(poiItem.getPoiExtension().getmRating());
        binding.tvPoiDetailsAddress.setSelected(true);
    }

    private void iniEvent() {
        binding.llDetailsCall.setOnClickListener(this);
        binding.llDetailsEmail.setOnClickListener(this);
        binding.llDetailsWebSite.setOnClickListener(this);
        binding.fbPoiDetailsGo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbPoiDetailsGo:
                Navigation.findNavController(v).navigate(R.id.action_coffeeDetailsFragment_to_coffeeFragment);
                break;

            case R.id.llDetailsCall:
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

            case R.id.llDetailsEmail:

                break;

            case R.id.llDetailsWebSite:

                break;
        }
    }
}
