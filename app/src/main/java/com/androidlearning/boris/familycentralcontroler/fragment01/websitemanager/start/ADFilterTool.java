package com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.start;

import android.content.Context;
import android.content.res.Resources;

import com.androidlearning.boris.familycentralcontroler.R;

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