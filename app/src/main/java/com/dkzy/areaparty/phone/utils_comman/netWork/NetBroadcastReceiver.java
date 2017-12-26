package com.dkzy.areaparty.phone.utils_comman.netWork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.util.ArrayList;

/**
 * Created by borispaul on 17-5-15.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    public static ArrayList<netEventHandler> mListeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            MyApplication.setmNetWorkState(NetUtil.getNetWorkState(context));
            if(mListeners.size() > 0)
                for(netEventHandler handler : mListeners) {
                    handler.onNetChange();
                }
        }
    }

    public static abstract interface netEventHandler {
        public abstract void onNetChange();
    }
}
