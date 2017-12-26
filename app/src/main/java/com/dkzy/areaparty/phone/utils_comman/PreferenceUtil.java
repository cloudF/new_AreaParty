package com.dkzy.areaparty.phone.utils_comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Created by poxiaoge on 2016/12/25.
 */

public class PreferenceUtil {

    private SharedPreferences sp;
    public PreferenceUtil(Context mContext) {
        this.sp=mContext.getSharedPreferences("MobilePrefs", Context.MODE_PRIVATE);
    }

    public PreferenceUtil(String fileName, Context mContext) {
        this.sp=mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public void write(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.putString(key, "");
        }
        editor.apply();
        Log.e("write preference", "key=" + key + ",value=" + value);
    }
    public void writeBoole(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();

            editor.putBoolean(key, value);

        editor.apply();
        Log.e("write preference", "key=" + key + ",value=" + value);
    }

    public void writeAll(Map<String, String> map) {
        SharedPreferences.Editor editor = sp.edit();
        for (String key:map.keySet()) {
            editor.putString(key, map.get(key));
            Log.e("write preference", "key=" + key + ",value=" + map.get(key));
        }
        editor.apply();
    }

    public String read(String key) {
        return sp.getString(key,"");
    }
    public boolean readBoole(String key) {
        return sp.getBoolean(key,false);
    }

    public Map<String,?> getAll(){
        return sp.getAll();
    }

    public void remove(String key){
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }
}
