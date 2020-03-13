package com.dev.aurora.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.dev.aurora.R;
import com.dev.aurora.adapter.RVResultAdapter;
import com.dev.aurora.databinding.CoffeeSearchFragmentBinding;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;

import java.util.List;

public class CoffeeSearchFragment extends Fragment implements SearchView.OnQueryTextListener,
        Inputtips.InputtipsListener, SearchView.OnCloseListener {

    private CoffeeSearchFragmentBinding binding;

    private String cityCode;
    private RVResultAdapter rvAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        System.out.println("Coffee-onDestroyView");

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
        binding.svSearch.onActionViewExpanded();
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireActivity());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        rvAdapter = new RVResultAdapter();
        binding.rvSearch.setAdapter(rvAdapter);
        binding.rvSearch.setLayoutManager(linearLayout);
        binding.rvSearch.addItemDecoration(itemDecoration);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void initEvent() {
        binding.svSearch.setOnQueryTextListener(this);
        binding.svSearch.setOnCloseListener(this);
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
