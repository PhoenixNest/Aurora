package com.dev.aurora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.aurora.R;
import com.dev.aurora.databinding.RvSearchItemHistoryNormalBinding;
import com.dev.aurora.db.SearchItem;

import java.util.Objects;

public class RVSearchHistoryAdapter extends ListAdapter<SearchItem, RecyclerView.ViewHolder> {

    private RVSearchHistoryAdapter.onItemClickListener listener;

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setListener(RVSearchHistoryAdapter.onItemClickListener listener) {
        this.listener = listener;
    }

    public RVSearchHistoryAdapter() {
        super(diffCallBack);
    }

    private static DiffUtil.ItemCallback<SearchItem> diffCallBack = new DiffUtil.ItemCallback<SearchItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull SearchItem oldItem, @NonNull SearchItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchItem oldItem, @NonNull SearchItem newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (getItemCount() == 0) {
            return new RVSearchHistoryNothingVH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_search_item_history_nothing, parent, false));
        } else {
            return new RVSearchHistoryNormalVH(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.rv_search_item_history_normal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RVSearchHistoryNormalVH) {
            SearchItem item = getItem(position);
            ((RVSearchHistoryNormalVH) holder).bindData(item);
            ((RVSearchHistoryNormalVH) holder).getBinding().tvSearchTime.setSelected(true);
            ((RVSearchHistoryNormalVH) holder).itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class RVSearchHistoryNothingVH extends RecyclerView.ViewHolder {

        RVSearchHistoryNothingVH(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class RVSearchHistoryNormalVH extends RecyclerView.ViewHolder {

        RvSearchItemHistoryNormalBinding binding;

        RVSearchHistoryNormalVH(RvSearchItemHistoryNormalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        RvSearchItemHistoryNormalBinding getBinding() {
            return binding;
        }

        void bindData(SearchItem searchItem) {
            binding.setData(searchItem);
        }
    }

}
