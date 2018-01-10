package com.dkzy.areaparty.phone.fragment01.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dkzy.areaparty.phone.FileTypeConst;
import com.dkzy.areaparty.phone.fragment01.downloadedPicBrowseActivity;
import com.dkzy.areaparty.phone.fragment01.model.DownloadFileModel;
import com.dkzy.areaparty.phone.fragment01.model.downloadedFileBean;
import com.dkzy.areaparty.phone.fragment02.contentResolver.FileItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.lzy.okgo.model.Progress;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by borispaul on 17-5-10.
 */

public class DownloadFileManagerHelper {
    public static final int REFRESHTAB02 = 1;
    public static final int DLNASUCCESSFUL = 2;
    public static final int DLNAFAITH = 3;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/areaparty/downloadedfiles/";
    private static List<downloadedFileBean> localFiles = new ArrayList<>();
    private static Handler tab02Handle;

    public static void setTab02Handle(Handler tab02Handle) {
        DownloadFileManagerHelper.tab02Handle = tab02Handle;
    }

    public static void addLocalFiles(downloadedFileBean file) {
        file.setPath(localPath + file.getName());
        if(file.getFileType() == FileTypeConst.music || file.getFileType() == FileTypeConst.video) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getPath());
                mediaPlayer.prepare();
                file.setTimeLength(mediaPlayer.getDuration() / 1000);
                Log.e("test", file.getName() + mediaPlayer.getDuration());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(localFiles != null)
            localFiles.add(file);
        else {
            localFiles = new ArrayList<>();
            localFiles.add(file);
        }

        Message message = new Message();
        message.what = REFRESHTAB02;
        tab02Handle.sendMessage(message);
    }

    public static boolean isFileExist(String name) {
        try{
            for(downloadedFileBean file : localFiles) {
                if(file.getName().equals(name))
                    return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    public static String getPathFromName(String name) {
        for(downloadedFileBean file : localFiles) {
            if(file.getName().equals(name))
                return file.getPath();
        }
        return "";
    }

    public static void dlnaCast(final downloadedFileBean file) {
        new Thread() {
            @Override
            public void run() {
                boolean state = prepareDataForFragment.getDlnaCastState(file);
                Message message = new Message();
                if(state) {
                    message.what = DLNASUCCESSFUL;
                } else message.what = DLNAFAITH;
                tab02Handle.sendMessage(message);
            }
        }.start();
    }
    public static void dlnaCast(final String type) {//tv可移动磁盘投屏
        if (MyApplication.isSelectedTVOnline()){
            new Thread() {
                @Override
                public void run() {
                    boolean state = prepareDataForFragment.getDlnaCastState(type);
                    try{
                        Looper.prepare();
                        if(state) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }.start();
        }else {
            Toasty.warning(MyApplication.getContext(), "当前电视不在线", Toast.LENGTH_SHORT, true).show();
        }
    }

    public static void dlnaCast(final FileItem file, final String type) {
        if (MyApplication.isSelectedTVOnline()){
            new Thread() {
                @Override
                public void run() {
                    boolean state = prepareDataForFragment.getDlnaCastState(file,type);
                    try{
                        Looper.prepare();
                        if(state) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Toasty.warning(MyApplication.getContext(), "当前电视不在线", Toast.LENGTH_SHORT, true).show();
        }

    }
    public static void dlnaCast(final String folderName, final String type) {
        if (TextUtils.isEmpty(folderName)){Toasty.error(MyApplication.getContext(), "投屏失败", Toast.LENGTH_SHORT).show();}
        if (MyApplication.isSelectedTVOnline()){
            new Thread() {
                @Override
                public void run() {
                    boolean state = prepareDataForFragment.getDlnaCastState(folderName,type);
                    try{
                        Looper.prepare();
                        if(state) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Toasty.warning(MyApplication.getContext(), "当前电视不在线", Toast.LENGTH_SHORT, true).show();
        }
    }
    public static void dlnaCast(final List<FileItem> setList, final String type) {
        if (MyApplication.isSelectedTVOnline()){
            new Thread() {
                @Override
                public void run() {
                    boolean state = prepareDataForFragment.getDlnaCastState(setList,type);
                    try{
                        Looper.prepare();
                        if(state) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Toasty.warning(MyApplication.getContext(), "当前电视不在线", Toast.LENGTH_SHORT, true).show();
        }
    }
    public static void dlnaCast(final List<FileItem> setList, final String type, boolean asbgm) {
        if (!asbgm) return;
        if (MyApplication.isSelectedTVOnline()){
            new Thread() {
                @Override
                public void run() {
                    boolean state = prepareDataForFragment.getDlnaCastState_bgm(setList,type);
                    try{
                        Looper.prepare();
                        if(state) {
                            handler.sendEmptyMessage(1);
                        } else {
                            handler.sendEmptyMessage(2);
                        }
                        Looper.loop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Toasty.warning(MyApplication.getContext(), "当前电视不在线", Toast.LENGTH_SHORT, true).show();
        }
    }



    /**
     * <summary>
     *  删除localPath路径下的指定文件
     * </summary>
     * <param name="activity">文件名称</param>
     * <returns>删除是否成功</returns>
     */
    public static boolean removeFile(String name) {
        File file = new File(localPath + name);
        if(file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static List<downloadedFileBean> getLocalFiles() {
        return localFiles;
    }

    public static void addLocalFiles(List<Progress> allDownloadingFiles) {
        File dir = new File(localPath);
        if(!dir.exists())
            dir.mkdirs();
        localFiles.clear();
        File[] allFiles = new File(localPath).listFiles();
        for(int i = 0; i < allFiles.length; ++i) {
            if(allFiles[i].isFile()) {
                if(!isDownloading(allDownloadingFiles, allFiles[i].getName())) {
                    String name = allFiles[i].getName();
                    String sizeInfor = formatFileSize(allFiles[i].length());
                    long lastChangeTime = new Date(allFiles[i].lastModified()).getTime();
                    int durationTime = 0;
                    int fileType = FileTypeConst.determineFileType(name);
                    String path = localPath + name;
                    downloadedFileBean file = new downloadedFileBean(name, sizeInfor, lastChangeTime, durationTime, fileType, path);
                    localFiles.add(file);
                }
            }
        }
        Log.e("circle1", "addFile" + localFiles.size());
    }

    public static void getDuration() {
        try {
            for(downloadedFileBean file : localFiles) {
                if(file.getFileType() == FileTypeConst.music || file.getFileType() == FileTypeConst.video) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(file.getPath());
                    mediaPlayer.prepare();
                    file.setTimeLength(mediaPlayer.getDuration() / 1000);
                    Log.e("test", file.getName() + mediaPlayer.getDuration());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Intent openApkFileIntent(String filepath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(filepath));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    public static Intent openVideoFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    public static Intent openAudioFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    public static Intent openPicFileIntent(String param, Context context) {
        Bundle data = new Bundle();
        data.putString("imgPath", param);
        Intent intent = new Intent(context, downloadedPicBrowseActivity.class);
        intent.putExtras(data);
        return intent;
    }

    public static Intent openPPTFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    public static Intent openExcelFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    public static Intent openWordFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    public static Intent openTextFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    public static Intent openPdfFileIntent(String param){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * <summary>
     *  判断指定文件是否正在下载
     * </summary>
     * <param name="allFiles">正在下载的文件</param>
     * <param name="fileName">要判断的文件名称</param>
     * <returns>删除是否成功</returns>
     */
    private static boolean isDownloading(List<Progress> allFiles, String fileName) {
        for(Progress downloadInfo : allFiles) {
            if(((DownloadFileModel)(downloadInfo.extra1)).getName().equals(fileName))
                return true;
        }
        return false;
    }

    private static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString = "(" + df.format((double) size) + "B)";
        } else if (size < 1048576) {
            fileSizeString = "(" + df.format((double) size / 1024) + "KB)";
        } else if (size < 1073741824) {
            fileSizeString = "(" + df.format((double) size / 1048576) + "MB)";
        } else {
            fileSizeString = "(" + df.format((double) size / 1073741824) + "GB)";
        }
        return fileSizeString;
    }

    public static Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:Toasty.info(MyApplication.getContext(), "投屏成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:Toasty.error(MyApplication.getContext(), "投屏失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
