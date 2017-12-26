package com.dkzy.areaparty.phone.fragment01.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.simple.eventbus.EventBus;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by borispaul on 17-7-11.
 */

public class tvpcAppHelper {
    public static ArrayList<AppItem> tvApps = new ArrayList<>();
    public static ArrayList<AppItem> pcApps = new ArrayList<>();

    public static void initPCApps() {
        pcApps.clear();
        Gson gson = new Gson();
        if(MyApplication.getSelectedPCIP() != null&& !TextUtils.isEmpty(MyApplication.getSelectedPCIP().mac)) {
            String pcAppStr = new PreferenceUtil(MyApplication.getContext()).read(MyApplication.getSelectedPCIP().mac + "pcApps");
            if(pcAppStr.length() > 0) {
                JsonReader reader = new JsonReader(new StringReader(pcAppStr));
                try {
                    List<AppItem> list = gson.fromJson(reader, new TypeToken<List<AppItem>>(){}.getType());
                    pcApps.addAll(list);
                } catch (Exception e) {
                    Log.i("tvpcAppHelper", "获取信息出错");
                }
            }
        }
        EventBus.getDefault().post(pcApps, "PCAppInitialed");
    }

    public static void initTVApps() {
        tvApps.clear();
        Gson gson = new Gson();
        if(MyApplication.getSelectedTVIP() != null && !MyApplication.getSelectedTVIP().mac.equals("")) {
            String tvAppStr = new PreferenceUtil(MyApplication.getContext()).read(MyApplication.getSelectedTVIP().mac + "tvApps");
            if(tvAppStr.length() > 0) {
                JsonReader reader = new JsonReader(new StringReader(tvAppStr));
                try {
                    List<AppItem> list = gson.fromJson(reader, new TypeToken<List<AppItem>>(){}.getType());
                    tvApps.addAll(list);
                } catch (Exception e) {
                    Log.i("tvpcAppHelper", "获取信息出错");
                }
            }
        }
        EventBus.getDefault().post(tvApps, "TVAppInitialed");
    }

    public static void addTVApps(AppItem currentApp) {
        if(!isContained(currentApp, tvApps)) {
            if(tvApps.size() < 8) {
                tvApps.add(0, currentApp);
            } else {
                tvApps.remove(tvApps.size() - 1);
                tvApps.add(0, currentApp);
            }
            String tvAppStr = JsonUitl.objectToString(tvApps);
            new PreferenceUtil(MyApplication.getContext()).write(MyApplication.getSelectedTVIP().mac + "tvApps", tvAppStr);
            EventBus.getDefault().post(tvApps, "TVAppAdded");
        }
    }

    public static void addPCApps(AppItem currentApp) {
        if(!isContained(currentApp, pcApps)) {
            if(pcApps.size() < 8) {
                pcApps.add(0, currentApp);
            } else {
                pcApps.remove(pcApps.size() - 1);
                pcApps.add(0, currentApp);
            }
            String pcAppStr = JsonUitl.objectToString(pcApps);
            new PreferenceUtil(MyApplication.getContext()).write(MyApplication.getSelectedPCIP().mac + "pcApps", pcAppStr);
            EventBus.getDefault().post(pcApps, "PCAppAdded");
        }
    }

    private static boolean isContained(AppItem item, List<AppItem> list) {
        for(int i = 0; i < list.size(); ++i) {
            if(list.get(i).getPackageName().equals(item.getPackageName()))
                return true;
        }
        return false;
    }
}
