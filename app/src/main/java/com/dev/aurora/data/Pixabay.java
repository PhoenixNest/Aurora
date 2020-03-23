package com.dev.aurora.data;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;

public interface Pixabay {
    @GET("?key={key}&image_type={image_type}&orientation={orientation}&editors_choice={editors_choice}&order={order}")
    Call<PixabayBean> getPixabayData(
            @Field("key") String key,
            @Field("image_type") String image_type,
            @Field("orientation") String orientation,
            @Field("editors_choice") String editors_choice,
            @Field("order") String order);
}

// Test Code
    /*public LiveData<PixabayBean> _getPixabayDataByRetrofit() {
        if (pixabayLiveData == null) {
            pixabayLiveData = new MutableLiveData<>();
            pixabayLiveData.setValue(null);
        }

        Pixabay call = RetrofitUtils.getInstance(ConstUtils.baseURL).create(Pixabay.class);
        call.getPixabayData(ConstUtils.pixabayKey,
                ConstUtils.pixabayImageType,
                ConstUtils.pixabayImageOrientation,
                ConstUtils.pixabayEditorsChoice,
                ConstUtils.pixabayOrderBy).enqueue(new Callback<PixabayBean>() {
            @Override
            public void onResponse(Call<PixabayBean> call, Response<PixabayBean> response) {
                Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片成功" + response.body().toString());
                pixabayLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<PixabayBean> call, Throwable throwable) {
                Log.d(ConstUtils.coffeeMainFragmentName, "获取封面图片失败：" + throwable);
            }
        });

        return pixabayLiveData;
    }*/