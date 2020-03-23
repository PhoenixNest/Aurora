package com.dev.aurora.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.bumptech.glide.Glide;
import com.dev.aurora.R;
import com.dev.aurora.databinding.RvPoiItemNormalBinding;

import java.util.List;

public class RVMainPoiAdapter extends PagedListAdapter<PoiItem, RecyclerView.ViewHolder> {
    private LatLng startLatLng;
    private onItemClickListener listener;

    public RVMainPoiAdapter() {
        super(diffCallBack);
    }

    private static DiffUtil.ItemCallback<PoiItem> diffCallBack = new DiffUtil.ItemCallback<PoiItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
            return oldItem.getPoiId().equals(newItem.getPoiId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void setStartLatLng(LatLng startLatLng) {
        this.startLatLng = startLatLng;
    }

    public List<PoiItem> getPoiItemList() {
        return getCurrentList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RVPoiVH(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.rv_poi_item_normal, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PoiItem poiItem = getItem(position);
        LatLng endLatLng;
        if (poiItem != null) {
            endLatLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            int distance = (int) AMapUtils.calculateLineDistance(startLatLng, endLatLng);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(distance).append("M");

            ((RVPoiVH) holder).bindData(poiItem);
            ((RVPoiVH) holder).getBinding().executePendingBindings();
            ((RVPoiVH) holder).getBinding().tvPoiName.setSelected(true);
            ((RVPoiVH) holder).getBinding().tvType.setSelected(true);
            ((RVPoiVH) holder).getBinding().tvPoiDistance.setText(stringBuilder);
            if (!TextUtils.isEmpty(poiItem.getPoiExtension().getmRating())) {
                ((RVPoiVH) holder).getBinding().rbPoi.setRating(Float.parseFloat(poiItem.getPoiExtension().getmRating()));
            }

            if (poiItem.getPhotos().size() > 0) {
                Glide.with(((RVPoiVH) holder).getBinding().getRoot())
                        .load(poiItem.getPhotos().get(0).getUrl())
                        .placeholder(R.drawable.ic_loading_gray_72dp)
                        .error(R.drawable.ic_error_red_72dp)
                        .timeout(3000)
                        .into(((RVPoiVH) holder).getBinding().ivPoi);
            } else {
                ((RVPoiVH) holder).getBinding().ivPoi.setImageResource(R.drawable.ic_nothing_gray_72dp);
            }

            ((RVPoiVH) holder).itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(position);
                }
            });
        }
    }

    static class RVPoiVH extends RecyclerView.ViewHolder {
        RvPoiItemNormalBinding binding;

        RVPoiVH(@NonNull RvPoiItemNormalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        RvPoiItemNormalBinding getBinding() {
            return binding;
        }

        void bindData(PoiItem poiItem) {
            binding.setData(poiItem);
        }
    }
}
