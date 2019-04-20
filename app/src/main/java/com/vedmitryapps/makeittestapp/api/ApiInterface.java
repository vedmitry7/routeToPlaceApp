package com.vedmitryapps.makeittestapp.api;

import com.vedmitryapps.makeittestapp.api.models.PlacesResponce;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("place/search/json")
    Call<PlacesResponce> getPlaces(@Query("key") String keyApi,
                                   @Query("location") String location,
                                   @Query("radius") int radius);

}