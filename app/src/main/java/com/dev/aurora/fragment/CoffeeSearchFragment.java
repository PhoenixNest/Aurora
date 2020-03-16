package com.dev.aurora.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.dev.aurora.R;
import com.dev.aurora.adapter.RVSearchResultAdapter;
import com.dev.aurora.databinding.CoffeeSearchFragmentBinding;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;
import com.dev.aurora.viewmodel.CoffeeViewModel;

import java.util.List;

public class CoffeeSearchFragment extends Fragment implements SearchView.OnQueryTextListener,
        Inputtips.InputtipsListener, SearchView.OnCloseListener {

    private CoffeeSearchFragmentBinding binding;
    private CoffeeViewModel mViewModel;

    private String cityCode;
    private RVSearchResultAdapter rvAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        System.out.println("Search-onCreateView");
        mViewModel = new ViewModelProvider(requireActivity()).get(CoffeeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_search_fragment, container, false);
        binding.setLifecycleOwner(this);

        initUi();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            cityCode = getArguments().getString(ConstUtils.KEY_cityCode);
        }

        initEvent();
    }

    private void initUi() {
        int statusBarHeight = SysUtils.getStatusHeight(requireActivity());
        binding.consSearch.setPadding(0, statusBarHeight, 0, 0);
        binding.consSearch.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));

        binding.svSearch.onActionViewExpanded();
        int hintTextId = binding.svSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView hintText = binding.svSearch.findViewById(hintTextId);
        hintText.setHintTextColor(Color.parseColor("#80FFFFFF"));
        hintText.setTextColor(Color.WHITE);

        LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        rvAdapter = new RVSearchResultAdapter();
        binding.rvSearch.setAdapter(rvAdapter);
        binding.rvSearch.setLayoutManager(linearLayout);
        binding.rvSearch.addItemDecoration(itemDecoration);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void initEvent() {
        binding.svSearch.setOnQueryTextListener(this);
        binding.svSearch.setOnCloseListener(this);
        rvAdapter.setListener(position -> {
            mViewModel.setTipData(rvAdapter.getCurrentList().get(position));
            Navigation.findNavController(binding.rvSearch).navigateUp();
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            InputtipsQuery query = new InputtipsQuery(newText, cityCode);
            query.setCityLimit(true);
            Inputtips inputtips = new Inputtips(requireActivity(), query);
            inputtips.setInputtipsListener(this);
            inputtips.requestInputtipsAsyn();
        } else {
            rvAdapter.submitList(null);
        }

        return true;
    }

    @Override
    public void onGetInputtips(List<Tip> list, int rCode) {
        if (rCode == 1000) {
            rvAdapter.submitList(list);
        }
    }

    @Override
    public boolean onClose() {
        rvAdapter.submitList(null);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Search-onDestroyView");
        SysUtils.closeKeyBoard(requireActivity().getApplicationContext(), getView());
        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Search-onDestroy");
    }
}
