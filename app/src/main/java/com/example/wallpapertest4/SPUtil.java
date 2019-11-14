package com.example.wallpapertest4;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {
    private static String getPacName(Context context){
        return context.getPackageName();
    }

    public static void put(Context context, String key, boolean value){
        SharedPreferences sp = context.getSharedPreferences(getPacName(context),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean get(Context context, String key, boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(getPacName(context),Context.MODE_PRIVATE);
        return sp.getBoolean(key,defaultValue);
    }
}
