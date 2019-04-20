package com.vedmitryapps.makeittestapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedManager {

    public static final String STORAGE_NAME = "app";

    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context = null;

    public static void init(Context context ){
        SharedManager.context = context;
    }

    private static void init(){
        sharedPreferences = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void addProperty(String name, String value){
        if( sharedPreferences == null ){
            init();
        }
        editor.putString( name, value );
        editor.commit();
    }

    public static String getProperty(String name){
        if( sharedPreferences == null ){
            init();
        }
        return sharedPreferences.getString( name, null );
    }
}