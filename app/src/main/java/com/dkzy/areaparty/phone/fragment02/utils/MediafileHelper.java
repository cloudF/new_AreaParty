package com.dkzy.areaparty.phone.fragment02.utils;

import android.os.Handler;
import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_playPicList;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.utils_comman.ReceiveCommandFromTVPlayer;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.CommandUtil;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;
import com.dkzy.areaparty.phone.utils_comman.Send2TVThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by borispaul on 17-6-22.
 */

public class MediafileHelper {
    public static ArrayList<MediaItem> recentAudios = new ArrayList<>(); // 最近播放的音频文件
    public static ArrayList<MediaItem> recentVideos = new ArrayList<>(); // 最近播放的视频文件
    public static Map<String, List<MediaItem>> audioSets = new HashMap<>();  // 音频集
    public static Map<String, List<MediaItem>> imageSets = new HashMap<>();  // 图片集

    // 进入相应媒体库时使用
    private static String mediaType = "";    // VIDEO, AUDIO, IMAGE
    private static List<String> startPathList = new ArrayList<>();
    private static String currentPath = "";  // 当前路径
    public static ArrayList<MediaItem> mediaFiles = new ArrayList<>();   // 当前路径下的媒体文件
    public static ArrayList<MediaItem> mediaFolders = new ArrayList<>(); // 当前路径下的文件夹


    /**
     * <summary>
     *  清空最近播放文件和播放列表信息
     *  PC设备切换时调用
     * </summary>
     */
    public static void resetTotalInfors() {
        recentAudios.clear();
        recentVideos.clear();
        audioSets.clear();
        imageSets.clear();
    }

    /**
     * <summary>
     *  清空媒体库文件信息
     *  从视频库、音频库或图片库返回媒体库页面(tab02)时调用
     * </summary>
     */
    public static void resetMediaInfors() {
        mediaType = "";
        currentPath = "";
        mediaFiles.clear();
        mediaFolders.clear();
        startPathList.clear();
    }

    public static void setMediaSets(Map<String, List<MediaItem>> tempSets, String typeName) {
        if(typeName.equals(OrderConst.audioAction_name)) {
            audioSets.clear();
            audioSets.putAll(tempSets);
        } else if(typeName.equals(OrderConst.imageAction_name)) {
            imageSets.clear();
            imageSets.putAll(tempSets);
        }
    }

    public static void setRecentFiles(List<MediaItem> tempFiles, String fileType) {
        if(fileType.equals(OrderConst.audioAction_name)) {
            recentAudios.clear();
            recentAudios.addAll(tempFiles);
        } else if(fileType.equals(OrderConst.videoAction_name)) {
            recentVideos.clear();
            recentVideos.addAll(tempFiles);
        }
    }

    public static void setMediaFiles(List<MediaItem> tempMediaFiles, List<MediaItem> folders) {
        mediaFiles.clear();
        mediaFolders.clear();
        mediaFiles.addAll(tempMediaFiles);
        mediaFolders.addAll(folders);
        if(startPathList.size() == 0) {
            if(folders.size() > 0) {
                for(int i = 0; i < folders.size(); ++i) {
                    String startPath = folders.get(i).getPathName();
                    startPath = startPath.substring(0, startPath.length() - folders.get(i).getName().length() - 1);
                    startPathList.add(startPath);
                }

            }
            for (int i = 0; i < startPathList.size(); ++i)
                Log.i("Send2PCThread", "初始路径" + i + "---" + startPathList.get(i));
        }
    }

    /**
     * <summary>
     *  启动线程从pc下载图片集、音频集
     *  <param name="handler">消息传递句柄</param>
     * </summary>
     */
    public static void loadMediaSets(Handler handler) {
        audioSets.clear();
        imageSets.clear();
        new Send2PCThread(OrderConst.audioAction_name, OrderConst.mediaAction_getSets_command, handler).start();
        new Send2PCThread(OrderConst.imageAction_name, OrderConst.mediaAction_getSets_command, handler).start();
    }

    /**
     * <summary>
     *  播放音频成功后调用将当前音频文件添加到最近播放文件中
     *  <param name="file">当前播放着的音频</param>
     * </summary>
     */
    public static void addRecentAudios(MediaItem file) {
        for(int i = 0; i < recentAudios.size(); ++i) {
            if(recentAudios.get(i).equals(file))
                recentAudios.remove(i--);
        }
        recentAudios.add(0, file);
    }

    /**
     * <summary>
     *  播放视频成功后调用将当前视频文件添加到最近播放文件中
     *  <param name="file">当前播放着的音频</param>
     * </summary>
     */
    public static void addRecentVideos(MediaItem file) {
        for(int i = 0; i < recentVideos.size(); ++i) {
            if(recentVideos.get(i).equals(file))
                recentVideos.remove(i--);
        }
        recentVideos.add(0, file);
    }

    /**
     * <summary>
     *  启动线程从pc下载最近播放文件(视频和音频)
     *  每次初始化或PC切换时调用
     *  <param name="handler">消息传递句柄</param>
     * </summary>
     */
    public static void loadRecentMediaFiles(Handler handler) {
        recentAudios.clear();
        recentVideos.clear();
        new Send2PCThread(OrderConst.audioAction_name, OrderConst.appMediaAction_getRecent_command, handler).start();
        new Send2PCThread(OrderConst.videoAction_name, OrderConst.appMediaAction_getRecent_command, handler).start();
    }

    /**
     * <summary>
     *  启动线程从pc下载对应媒体库文件列表
     *  进入相应媒体库时调用
     * </summary>
     * <param name="handler">消息传递句柄</param>
     */
    public static void loadMediaLibFiles(Handler handler) {
        mediaFiles.clear();
        mediaFolders.clear();
        boolean isRoot = true;
        if(startPathList.size() != 0)
            isRoot = isPathContained(currentPath, startPathList);
        new Send2PCThread(mediaType, currentPath, isRoot, handler).start();
    }
    public static void deleteMediaLibFiles(Handler handler, String path) {
        new Send2PCThread(mediaType, path, false, handler).start();
    }

    public static boolean isPathContained(String path, List<String> lists) {
        if(lists.size() == 0)
            return true;
        if(path.equals(""))
            return true;
        for(int i = 0; i < lists.size(); ++i)
            if(path.equals(lists.get(i)))
                return true;
        return false;
    }

    /**
     * <summary>
     *  启动线程播放PC上指定的媒体文件到指定TV的
     * </summary>
     * <param name="filetype">媒体文件类别(AUDIO、VIDEO、IMAGE)</param>
     * <param name="filename">媒体文件名称</param>
     * <param name="path">媒体文件路径信息</param>
     * <param name="tvname">tv名称</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public static void playMediaFile(String filetype, String path, String filename, String tvname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("path", path);
        param.put("filename", filename);
        param.put("tvname", tvname);
        new Send2PCThread(filetype, OrderConst.mediaAction_play_command, param, myhandler).start();
        if (!ReceiveCommandFromTVPlayer.getPlayerIsRun()){
            new ReceiveCommandFromTVPlayer(true).start();
//            EventBus.getDefault().post(new TvPlayerChangeEvent(true), "tvPlayerStateChanged");
        }
    }
    public static void playAllMediaFile(String filetype, String folderPath, String tvname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("folder", folderPath);
        param.put("tvname", tvname);
        param.put("t",""+ ActionDialog_playPicList.t);
        new Send2PCThread(filetype, OrderConst.mediaAction_playALL_command, param, myhandler).start();
        if (!ReceiveCommandFromTVPlayer.getPlayerIsRun()){
            new ReceiveCommandFromTVPlayer(true).start();
//            EventBus.getDefault().post(new TvPlayerChangeEvent(true), "tvPlayerStateChanged");
        }
    }



    /**
     * <summary>
     *  启动线程播放PC上指定的媒体列表到指定TV
     * </summary>
     * <param name="filetype">媒体文件类别(AUDIO、IMAGE)</param>
     * <param name="filename">媒体文件名称</param>
     * <param name="path">媒体文件路径信息</param>
     * <param name="tvname">tv名称</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public static void playMediaSet(String setType, String setName, String tvname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setName);
        param.put("tvname", tvname);
        new Send2PCThread(setType, OrderConst.mediaAction_playSet_command, param, myhandler).start();
    }

    public static void playMediaSetAsBGM(String setType, String setName, String tvname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setName);
        param.put("tvname", tvname);
        new Send2PCThread(setType, OrderConst.mediaAction_playSet_command_BGM, param, myhandler).start();
    }


    public static void vlcContinue() {
        new Send2TVThread(JsonUitl.objectToString(CommandUtil.createPlayVLCCommand())).start();
    }

    public static void vlcPause() {
        new Send2TVThread(JsonUitl.objectToString(CommandUtil.createPauseVLCCommand())).start();
    }

    /**
     * <summary>
     *  启动线程向PC上增加指定类别的集合
     *  在相应的媒体库调用
     * </summary>
     * <param name="setname">要新增的播放列表名称</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public static void addPlaySet(String setname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setname);
        new Send2PCThread(mediaType, OrderConst.mediaAction_addSet_command, param, myhandler).start();
    }

    /**
     * <summary>
     *  过滤选中的文件列表
     *  启动线程将剩余的文件添加到指定列表
     *  在相应的媒体库调用
     * </summary>
     * <param name="setname">指定的要添加当前文件的播放列表</param>
     * <param name="mediaList">要添加的当前文件列表</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public static void addFilesToSet(String setname, List<MediaItem> mediaList, Handler myhandler) {
        if(mediaType.equals(OrderConst.audioAction_name)) {
            List<MediaItem> currentSet = audioSets.get(setname);
            for(int i = 0; i < mediaList.size(); ++i)
                for(int j = 0; j < currentSet.size(); ++j)
                    if(i>=0 && mediaList.get(i).equals(currentSet.get(j)))
                        mediaList.remove(i--);
        } else if(mediaType.equals(OrderConst.imageAction_name)) {
            List<MediaItem> currentSet = imageSets.get(setname);
            for(int i = 0; i < mediaList.size(); ++i)
                for(int j = 0; j < currentSet.size(); ++j)
                    if(i>=0 && mediaList.get(i).equals(currentSet.get(j)))
                        mediaList.remove(i--);
        }
        if(mediaList.size() > 0) {
            String liststr = JsonUitl.objectToString(mediaList);
            Log.w("MediafileHelper liststr",liststr);
            Map<String, String> param = new HashMap<>();
            param.put("setname", setname);
            param.put("liststr", liststr);
            new Send2PCThread(mediaType, OrderConst.mediaAction_addFilesToSet_command, param, myhandler).start();
        } else myhandler.sendEmptyMessage(OrderConst.addPCFilesToSet_OK);
    }

    public static void addFileToLocalSet(String name, MediaItem file) {
        List<MediaItem> currentSet;
        if(mediaType.equals(OrderConst.audioAction_name)) {
            currentSet = audioSets.get(name);
        } else {
            currentSet = imageSets.get(name);
        }
        boolean state = false;
        for(int i = 0; i < currentSet.size(); ++i)
            if(currentSet.get(i).equals(file))
                state = true;
        if(!state)
            currentSet.add(file);
    }

    public static void addFileToLocalSet(String name, List<MediaItem> fileList) {
        List<MediaItem> currentSet;
        if(mediaType.equals(OrderConst.audioAction_name)) {
            currentSet = audioSets.get(name);
        } else {
            currentSet = imageSets.get(name);
        }
        for (MediaItem file : fileList){
            boolean state = false;
            for(int i = 0; i < currentSet.size(); ++i)
                if(currentSet.get(i).equals(file))
                    state = true;
            if(!state)
                currentSet.add(file);
        }

    }

    /**
     * <summary>
     *  启动线程向PC上增加音频播放的集合
     *  在音频集合页面调用
     * </summary>
     * <param name="filename">媒体文件名称</param>
     */
    public static void addAudioPlaySet(String setname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setname);
        new Send2PCThread(OrderConst.audioAction_name, OrderConst.mediaAction_addSet_command, param, myhandler).start();
    }

    /**
     * <summary>
     *  启动线程向PC上删除音频播放的集合
     *  在音频集合页面调用
     * </summary>
     * <param name="filename">媒体文件名称</param>
     */
    public static void deleteAudioPlaySet(String setname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setname);
        new Send2PCThread(OrderConst.audioAction_name, OrderConst.mediaAction_deleteSet_command, param, myhandler).start();
    }

    /**
     * <summary>
     *  启动线程向PC上增加图片播放的集合
     *  在音频集合页面调用
     * </summary>
     * <param name="filename">媒体文件名称</param>
     */
    public static void addImagePlaySet(String setname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setname);
        new Send2PCThread(OrderConst.imageAction_name, OrderConst.mediaAction_addSet_command, param, myhandler).start();
    }

    /**
     * <summary>
     *  启动线程向PC上删除图片播放的集合
     *  在图片集合页面调用
     * </summary>
     * <param name="filename">媒体文件名称</param>
     */
    public static void deleteImagePlaySet(String setname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("setname", setname);
        new Send2PCThread(OrderConst.imageAction_name, OrderConst.mediaAction_deleteSet_command, param, myhandler).start();
    }

    public static List<MediaItem> getRecentAudios() {
        return recentAudios;
    }

    public static ArrayList<MediaItem> getRecentVideos() {
        return recentVideos;
    }

    public static Map<String, List<MediaItem>> getAudioSets() {
        return audioSets;
    }

    public static Map<String, List<MediaItem>> getImageSets() {
        return imageSets;
    }

    public static String getMediaType() {
        return mediaType;
    }

    public static void setMediaType(String mediaType) {
        MediafileHelper.mediaType = mediaType;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static void setCurrentPath(String currentPath) {
        MediafileHelper.currentPath = currentPath;
        Log.i("Send2PCThread", "当前路径" + currentPath);
    }

    public static List<String> getStartPathList() {
        return startPathList;
    }

}
