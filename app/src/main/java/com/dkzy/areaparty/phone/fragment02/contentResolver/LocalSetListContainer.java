package com.dkzy.areaparty.phone.fragment02.contentResolver;

import android.util.ArrayMap;

import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.fragment02.Model.MediaSetBean;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuyulin on 2017/11/23.
 */

public class LocalSetListContainer {
    public static List<Integer> thumbnailIds = new ArrayList<Integer>(){{add(R.drawable.music1);add(R.drawable.music2);add(R.drawable.music3);add(R.drawable.music4);add(R.drawable.music5);add(R.drawable.music6);add(R.drawable.music7);add(R.drawable.music8);add(R.drawable.music9);add(R.drawable.music10);}};
    public static final Map<String,List<FileItem>> localMapList_audio = new ArrayMap<>();
    public static final Map<String,List<FileItem>> localMapList_image = new ArrayMap<>();

    public static List<MediaSetBean> getLocalSetList(String type){
        if (type.equals("audio")){localMapList_audio.clear();}else {localMapList_image.clear();}
        List<MediaSetBean> localSetList = new ArrayList<>();
        Map<String,?> setListMap = new PreferenceUtil("local_set_"+type, MyApplication.getContext()).getAll();
        for (Map.Entry<String, ?> entry : setListMap.entrySet()) {
            MediaSetBean file = new MediaSetBean();
            file.name = entry.getKey();

            String listStr = (String)  entry.getValue();
            List<FileItem> fileList;
            fileList = new Gson().fromJson(listStr, new TypeToken<List<FileItem>>(){}.getType());
            if (fileList == null){
                fileList = new ArrayList<>();
            } else {
                file.thumbnailURL = fileList.get(0).getmFilePath();
            }

            if (type.equals("audio")){
                localMapList_audio.put(file.name,fileList);
                file.thumbnailID = thumbnailIds.get((int)(Math.random() * 10));
                file.numInfor = fileList.size()+"首";
            }else {
                localMapList_image.put(file.name,fileList);
                file.thumbnailID = R.drawable.logo_imageset;
                file.numInfor = fileList.size()+"张";
            }

            localSetList.add(file);
        }
        return localSetList;
    }
    public static void addLocalSetList(String type,String setName){
        new PreferenceUtil("local_set_"+type, MyApplication.getContext()).write(setName,"");
    }
    public static void deleteLocalSetList(String type,String setName){
        new PreferenceUtil("local_set_"+type, MyApplication.getContext()).remove(setName);
    }
}
