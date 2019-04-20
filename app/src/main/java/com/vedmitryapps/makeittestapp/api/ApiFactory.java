package com.vedmitryapps.makeittestapp.api;

import com.vedmitryapps.makeittestapp.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static ApiInterface mApi;

    public static ApiInterface getApi(){
        if(mApi==null){

            Retrofit builder = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mApi = builder.create(ApiInterface.class);
        }
        return mApi;
    }
}
