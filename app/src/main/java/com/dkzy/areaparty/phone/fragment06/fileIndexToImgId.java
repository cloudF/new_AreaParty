package com.dkzy.areaparty.phone.fragment06;

import com.dkzy.areaparty.phone.R;

/**
 * Created by SnowMonkey on 2017/3/22.
 */

public class fileIndexToImgId {
    public static int toImgId(int headIndex) {
        switch (headIndex) {
            case 1:
                return R.drawable.folder;
            case 2:
                return R.drawable.excel;
            case 3:
                return R.drawable.music;
            case 4:
                return R.drawable.pdf;
            case 5:
                return R.drawable.ppt;
            case 6:
                return R.drawable.video;
            case 7:
                return R.drawable.word;
            case 8:
                return R.drawable.zip;
            case 9:
                return R.drawable.txt;
            case 10:
                return R.drawable.pic;
            default:
                return R.drawable.none;
        }
    }
}
