package com.dev.aurora.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.help.Tip;
import com.dev.aurora.R;
import com.dev.aurora.databinding.RvItemSearchBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RVResultAdapter extends ListAdapter<Tip, RVResultAdapter.RVSearchVH> {

    public RVResultAdapter() {
        super(new DiffUtil.ItemCallback<Tip>() {
            @Override
            public boolean areItemsTheSame(@NonNull Tip oldItem, @NonNull Tip newItem) {
                return oldItem.getPoiID().equals(newItem.getPoiID());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Tip oldItem, @NonNull Tip newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

    @NonNull
    @Override
    public RVSearchVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RVSearchVH holder = new RVSearchVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_search, parent, false));
        holder.itemView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RVSearchVH holder, int position) {
        Tip tip = getItem(position);
        holder.bindData(tip);
    }

    static class RVSearchVH extends RecyclerView.ViewHolder {
        private RvItemSearchBinding binding;

        RVSearchVH(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        void bindData(@NotNull Tip tip) {
            binding.setData(tip);
        }
    }

}
