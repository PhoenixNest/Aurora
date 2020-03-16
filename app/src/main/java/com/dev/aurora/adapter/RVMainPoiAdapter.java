package com.dev.aurora.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.bumptech.glide.Glide;
import com.dev.aurora.R;
import com.dev.aurora.databinding.RvPoiItemNormalBinding;

import java.util.List;
import java.util.Objects;

public class RVMainPoiAdapter extends ListAdapter<PoiItem, RecyclerView.ViewHolder> {
    private static final int normalViewType = 0;
    private static final int footViewType = 1;

    private LatLng startLatLng;
    private onItemClickListener listener;

    public RVMainPoiAdapter() {
        super(new DiffUtil.ItemCallback<PoiItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
                return oldItem.getPoiId().equals(newItem.getPoiId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
                return Objects.equals(oldItem, newItem);
            }
        });
    }

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
        if (viewType == footViewType) {
            return new RVPoiFoot(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_poi_item_foot, parent, false));
        } else {
            return new RVPoiVH(DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), R.layout.rv_poi_item_normal, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RVPoiFoot) {
            return;
        } else {

            PoiItem poiItem = getItem(position);
            LatLng endLatLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
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

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return footViewType;
        } else {
            return normalViewType;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    static class RVPoiVH extends RecyclerView.ViewHolder {
        RvPoiItemNormalBinding binding;

        RVPoiVH(RvPoiItemNormalBinding binding) {
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

    static class RVPoiFoot extends RecyclerView.ViewHolder {

        RVPoiFoot(@NonNull View itemView) {
            super(itemView);

        }
    }
}
