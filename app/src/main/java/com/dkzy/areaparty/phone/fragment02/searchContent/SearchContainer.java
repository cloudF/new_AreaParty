package com.dkzy.areaparty.phone.fragment02.searchContent;

import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyulin on 2017/11/28.
 */

public class SearchContainer {
    public static String history;

    public static List<FileItem> videoList = new ArrayList<>();
    public static List<FileItem> audioList = new ArrayList<>();
    public static List<FileItem> imageList = new ArrayList<>();

    public static List<MediaItem> videoList_pc = new ArrayList<>();
    public static List<MediaItem> audioList_pc = new ArrayList<>();
    public static List<MediaItem> imageList_pc = new ArrayList<>();



    public static void clear(){
        videoList.clear();
        audioList.clear();
        imageList.clear();

        videoList_pc.clear();
        audioList_pc.clear();
        imageList_pc.clear();
    }

    public static boolean isEmpty(){
        if (videoList.size() > 0 || audioList.size()>0 || imageList.size() >0 || videoList_pc.size()>0 || audioList_pc.size() >0 || imageList_pc.size() > 0){
            return false;
        }

        return true;
    }
}
