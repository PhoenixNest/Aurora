package com.dev.aurora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Tip;
import com.dev.aurora.R;
import com.dev.aurora.databinding.RvSearchItemNormalBinding;

import java.util.Objects;

public class RVSearchResultAdapter extends ListAdapter<Tip, RVSearchResultAdapter.RVSearchVH> {

    private onItemClickListener listener;

    public RVSearchResultAdapter() {
        super(diffCallBack);
    }

    private static DiffUtil.ItemCallback<Tip> diffCallBack = new DiffUtil.ItemCallback<Tip>() {
        @Override
        public boolean areItemsTheSame(@NonNull Tip oldItem, @NonNull Tip newItem) {
            return oldItem.getPoiID().equals(newItem.getPoiID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Tip oldItem, @NonNull Tip newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RVSearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RVSearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_search_item_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVSearchVH holder, int position) {
        Tip tip = getItem(position);
        holder.bindData(tip);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(position);
            }
        });
    }

    static class RVSearchVH extends RecyclerView.ViewHolder {
        private RvSearchItemNormalBinding binding;

        RVSearchVH(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindData(@NonNull Tip tip) {
            binding.setData(tip);
        }
    }

}
