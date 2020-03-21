package com.dev.aurora.fragment;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.dev.aurora.R;
import com.dev.aurora.adapter.RVSearchHistoryAdapter;
import com.dev.aurora.adapter.RVSearchResultAdapter;
import com.dev.aurora.databinding.CoffeeSearchFragmentBinding;
import com.dev.aurora.db.SearchItem;
import com.dev.aurora.utils.ConstUtils;
import com.dev.aurora.utils.SysUtils;
import com.dev.aurora.viewmodel.CoffeeViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CoffeeSearchFragment extends Fragment implements SearchView.OnQueryTextListener,
        Inputtips.InputtipsListener, SearchView.OnCloseListener {

    private CoffeeSearchFragmentBinding binding;
    private CoffeeViewModel mViewModel;

    private String cityCode;
    private RVSearchHistoryAdapter rvHistoryAdapter;
    private RVSearchResultAdapter rvTipAdapter;

    private List<SearchItem> searchItemList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(ConstUtils.coffeeSearchFragmentName, "---onCreateView---");

        mViewModel = new ViewModelProvider(requireActivity()).get(CoffeeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.coffee_search_fragment, container, false);
        binding.setLifecycleOwner(this);

        initUi();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ConstUtils.coffeeSearchFragmentName, "---onResume---");

        if (getArguments() != null) {
            cityCode = getArguments().getString(ConstUtils.KEY_cityCode);
        }

        initData();
        initEvent();
    }

    private void initUi() {
        int statusBarHeight = SysUtils.getInstance().getStatusHeight(requireActivity());
        binding.consSearch.setPadding(0, statusBarHeight, 0, 0);
        binding.consSearch.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary));

        // SearchView
        binding.svSearch.onActionViewExpanded();
        int hintTextId = binding.svSearch.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView hintText = binding.svSearch.findViewById(hintTextId);
        hintText.setHintTextColor(Color.parseColor("#80FFFFFF"));
        hintText.setTextColor(Color.WHITE);

        // RecyclerView
        LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);

        rvTipAdapter = new RVSearchResultAdapter();
        rvHistoryAdapter = new RVSearchHistoryAdapter();

        // 默认显示历史搜索列表
        binding.rvSearch.setAdapter(rvHistoryAdapter);

        binding.rvSearch.setLayoutManager(linearLayout);
        binding.rvSearch.addItemDecoration(itemDecoration);
        binding.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void initData() {
        mViewModel.getLiveSearchItemList().observe(getViewLifecycleOwner(), searchItems -> {
            int itemCount = rvHistoryAdapter.getItemCount();
            searchItemList = searchItems;
            if (itemCount != searchItems.size()) {
                if (itemCount < searchItems.size()) {
                    binding.rvSearch.smoothScrollBy(0, -200);
                }

                rvHistoryAdapter.submitList(searchItems);
            }
        });
    }

    private void initEvent() {
        binding.svSearch.setOnQueryTextListener(this);
        binding.svSearch.setOnCloseListener(this);
        rvHistoryAdapter.setListener(position -> {
            binding.svSearch.setQuery(rvHistoryAdapter.getCurrentList().get(position).getSearchName(), true);
            binding.rvSearch.setAdapter(rvTipAdapter);
        });

        rvTipAdapter.setListener(position -> {
            Tip tip = rvTipAdapter.getCurrentList().get(position);
            mViewModel.insertSearchItem(new SearchItem(tip.getName(), tip.getAddress()));
            if (tip.getPoint() != null) {
                mViewModel.setTipData(tip);
                Navigation.findNavController(binding.rvSearch).navigateUp();
            } else {
                Snackbar.make(binding.coorSearch, R.string.searchTipError, Snackbar.LENGTH_SHORT).show();
            }
        });

        bindRVDeletedSearchHistoryItem();
    }

    // RecyclerView SearchHistory Delete Animation
    private void bindRVDeletedSearchHistoryItem() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(ConstUtils.coffeeSearchFragmentName, "删除搜索记录为：" + searchItemList.get(viewHolder.getAdapterPosition()).getSearchName());
                mViewModel.deleteSingleSearchItem(searchItemList.get(viewHolder.getAdapterPosition()));
                Snackbar.make(binding.coorSearch, "删除成功", Snackbar.LENGTH_SHORT).show();
            }

            // 添加删除背景
            Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_clear_black_24dp);
            ColorDrawable backgroundColor = new ColorDrawable(Color.LTGRAY);

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;

                int iconLeft, iconRight, iconTop, iconBottom;
                int backTop, backBottom, backLeft, backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) {
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int) dX;
                    backgroundColor.setBounds(backLeft, backTop, backRight, backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else if (dX < 0) {
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    backgroundColor.setBounds(backLeft, backTop, backRight, backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                } else {
                    backgroundColor.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }
                backgroundColor.draw(canvas);
                icon.draw(canvas);
            }

        }).attachToRecyclerView(binding.rvSearch);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // 用户进行搜索并点击键盘回车按钮后插入历史搜索条目
        mViewModel.insertSearchItem(new SearchItem(query, ""));
        Log.d(ConstUtils.coffeeSearchFragmentName, "新增搜索条目为：" + query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // 用户进行搜索时更换RecyclerView的Adapter
        if (!TextUtils.isEmpty(newText.trim())) {
            InputtipsQuery query = new InputtipsQuery(newText, cityCode);
            query.setCityLimit(true);
            Inputtips inputtips = new Inputtips(requireActivity(), query);
            inputtips.setInputtipsListener(this);
            inputtips.requestInputtipsAsyn();
        } else {
            binding.rvSearch.setAdapter(rvHistoryAdapter);
            rvTipAdapter.submitList(null);
        }

        return true;
    }

    @Override
    public void onGetInputtips(List<Tip> list, int rCode) {
        if (rCode == 1000) {
            binding.rvSearch.setAdapter(rvTipAdapter);
            rvTipAdapter.submitList(list);
        }
    }

    @Override
    public boolean onClose() {
        binding.rvSearch.setAdapter(rvHistoryAdapter);
        rvHistoryAdapter.submitList(searchItemList);

        rvTipAdapter.submitList(null);
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(ConstUtils.coffeeSearchFragmentName, "---onDestroyView---");

        SysUtils.getInstance().closeKeyBoard(requireActivity().getApplicationContext(), getView());
        rvHistoryAdapter = null;
        rvTipAdapter = null;
        ((ViewGroup) binding.getRoot()).removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ConstUtils.coffeeSearchFragmentName, "---onDestroy---");
    }
}
