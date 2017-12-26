package com.dkzy.areaparty.phone.fragment01.websitemanager.start;

import android.content.Context;

import com.dkzy.areaparty.phone.R;

/**
 * Created by zhuyulin on 2017/11/16.
 */

public class ADFilterTool {
    public static boolean hasAd(Context context, String url) {
        String[] adUrls = context.getResources().getStringArray(R.array.adBlockUrl);
        for (String adUrl : adUrls) {
            if (url.contains(adUrl)) {
            return true;
        }
    }
    return false;
    }
}