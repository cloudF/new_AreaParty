package com.dkzy.areaparty.phone.fragment02.searchContent;

import android.os.Handler;
import android.os.Message;
;
import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataControl;
import com.dkzy.areaparty.phone.fragment02.contentResolver.ContentDataLoadTask;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;

import java.util.ArrayList;

/**
 * Created by zhuyulin on 2017/11/28.
 */

public class SearchThread extends Thread implements ContentDataLoadTask.OnContentDataLoadListener{
    private final String TAG = getClass().getSimpleName();
    private String words;

    private ContentDataLoadTask mContentDataLoadTask;
    private boolean localFinashed = false;
    private boolean pcFinashed = false;
    private Handler handlerReceive;


    public SearchThread(String words,Handler handlerReceive) {
        this.words = words;
        this.handlerReceive = handlerReceive;
    }

    @Override
    public void run() {
        super.run();
        SearchContainer.clear();//清空查询结果
        Log.w(TAG,"开始执行搜索");
        searchLocalMedia();//本地多媒体资源搜索
        searchPcMedia();

    }
    private void searchLocalMedia() {
        if (mContentDataLoadTask == null){
            ContentDataControl.clear();//清空本地媒体内容
            mContentDataLoadTask = new ContentDataLoadTask(MyApplication.getContext());
            mContentDataLoadTask.setmOnContentDataLoadListener(this);
            mContentDataLoadTask.execute();
        }
    }

    @Override
    public void onStartLoad() {

    }

    @Override
    public void onFinishLoad() {
//        Log.w(TAG,ContentDataControl.mVideoFolder.size()+"");
//        Log.w(TAG,ContentDataControl.mMusicFolder.size()+"");
//        Log.w(TAG,ContentDataControl.mPhotoFolder.size()+"");
        for (ArrayList<FileItem> list : ContentDataControl.mVideoFolder.values()){
            if (list!=null && list.size()>0){
                for (FileItem file: list){
                    if (file.getmFileName().contains(words)){
                        SearchContainer.videoList.add(file);
                    }
                }
            }
        }
        for (ArrayList<FileItem> list : ContentDataControl.mMusicFolder.values()){
            if (list!=null && list.size()>0){
                for (FileItem file: list){
                    if (file.getmFileName().contains(words)){
                        SearchContainer.audioList.add(file);
                    }
                }
            }
        }
        for (ArrayList<FileItem> list : ContentDataControl.mPhotoFolder.values()){
            if (list!=null && list.size()>0){
                for (FileItem file: list){
                    if (file.getmFileName().contains(words)){
                        SearchContainer.imageList.add(file);
                    }
                }
            }
        }
        localFinashed = true;
        report();
//        Log.w(TAG,SearchContainer.videoList.size()+"");
//        Log.w(TAG,SearchContainer.audioList.size()+"");
//        Log.w(TAG,SearchContainer.imageList.size()+"");
    }

    private void searchPcMedia() {
        if (MyApplication.isSelectedPCOnline()){
            new Send2PCThread(OrderConst.addPathToHttp_Name, OrderConst.Media_Search_By_Key, words, handler).start();
        }else {
            pcFinashed = true;
            report();
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case OrderConst.success:
                    pcFinashed = true;
                    report();
                    break;
                case OrderConst.failure:
                    pcFinashed = true;
                    report();
                    break;

            }
        }
    };

    private void report() {
        if (localFinashed && pcFinashed){
            handlerReceive.sendEmptyMessage(OrderConst.success);
            if (!SearchContainer.isEmpty()){
                SearchContainer.history = words;
            }
        }
    }

}
