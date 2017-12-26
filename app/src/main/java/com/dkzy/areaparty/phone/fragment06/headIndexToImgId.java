package com.dkzy.areaparty.phone.fragment06;

import com.dkzy.areaparty.phone.R;

/**
 * Created by SnowMonkey on 2017/3/13.
 */

public class headIndexToImgId {
    public static int toImgId(int headIndex){
        switch (headIndex){
            case 0:
                return R.drawable.tx1;
            case 1:
                return R.drawable.tx2;
            case 2:
                return R.drawable.tx3;
            case 3:
                return R.drawable.tx4;
            case 4:
                return R.drawable.tx5;
            default:
                return R.drawable.tx1;
        }
    }
}
