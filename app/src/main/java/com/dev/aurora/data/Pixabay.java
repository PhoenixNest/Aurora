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
