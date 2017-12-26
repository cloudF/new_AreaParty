package com.dkzy.areaparty.phone.fragment01.utorrent.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by poxiaoge on 2016/12/25.
 */

public class PreferenceUtil {

    private SharedPreferences sp;
    public PreferenceUtil(Context mContext) {
        this.sp=mContext.getSharedPreferences("utorrentPrefs", Context.MODE_PRIVATE);
    }

    public void write(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        if (value != null) {
            editor.putString(key, value);
        } else {
            editor.putString(key, "");
        }
        editor.apply();
        //Log.e("write preference", "key=" + key + ",value=" + value);
    }

    public void writeAll(Map<String, String> map) {
        SharedPreferences.Editor editor = sp.edit();
        for (String key:map.keySet()) {
            editor.putString(key, map.get(key));
            //Log.e("write preference", "key=" + key + ",value=" + map.get(key));
        }
        editor.apply();
    }

    public String read(String key) {
        return sp.getString(key,"");
    }







}
