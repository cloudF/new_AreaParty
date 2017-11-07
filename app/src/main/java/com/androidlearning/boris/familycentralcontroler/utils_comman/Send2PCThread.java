package com.androidlearning.boris.familycentralcontroler.utils_comman;

import android.os.Handler;
import android.util.Log;

import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.fragment02.Model.MediaItem;
import com.androidlearning.boris.familycentralcontroler.fragment02.utils.MediafileHelper;
import com.androidlearning.boris.familycentralcontroler.fragment03.Model.AppItem;
import com.androidlearning.boris.familycentralcontroler.fragment03.Model.PCInforBean;
import com.androidlearning.boris.familycentralcontroler.fragment03.utils.PCAppHelper;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.IPInforBean;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.JsonUitl;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by borispaul on 2017/6/23.
 * 获取PC的应用、媒体文件列表的线程
 */

public class Send2PCThread extends Thread {
    private static final int SOCKET_TIMEOUT = 5000;
    private String typeName;  // "SYS, APP, GAME, VIDEO, AUDIO, IMAGE"
    private String commandType = ""; // GETINFOR, GETTOTALLIST, GETRECENTLIST, GETSETS, OPEN_MIRACST, OPEN_RDP, PLAY, OPEN, ADDSET, ADDFILESTOSET
    private Map<String, String> param = new HashMap<>();
    private String path;  // 当类型是媒体库时, 该字段才有效
    private boolean isRoot; // 当类型是媒体库时, 该字段才有效

    private Handler myhandler;

    /**
     * <summary>
     *  构造函数
     * </summary>
     * <param name="typeName">类别名称(VIDEO, AUDIO, IMAGE, APP, GAME, SYS)</param>
     * <param name="commandType">操作类别</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public Send2PCThread(String typeName, String commandType, Handler myhandler) {
        this.typeName = typeName;
        this.commandType = commandType;
        this.myhandler = myhandler;
    }

    /**
     * <summary>
     *  构造函数
     * </summary>
     * <param name="typeName">类别名称(VIDEO, AUDIO, IMAGE, APP, GAME)</param>
     * <param name="commandType">操作类别</param>
     * <param name="param">参数</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public Send2PCThread(String typeName, String commandType, Map<String, String> param, Handler myhandler) {
        this.typeName = typeName;
        this.commandType = commandType;
        this.param = param;
        this.myhandler = myhandler;
    }

    /**
     * <summary>
     *  构造函数, 获取相应媒体库(PS视频库、音频库和图片库)
     * </summary>
     * <returns>网络状态</returns>
     */
    public Send2PCThread(String type, String path, boolean isRoot, Handler myhandler) {
        this.typeName = type;
        this.path = path;
        this.isRoot = isRoot;
        this.myhandler = myhandler;
        this.commandType = OrderConst.appMediaAction_getList_command;
    }



    @Override
    public void run() {
        IPInforBean pcIpInfor = MyApplication.getSelectedPCIP();
        if(pcIpInfor != null && !pcIpInfor.ip.equals("")) {
            String cmdStr = createCmdStr();
            String dataReceived = "";
            Socket client = new Socket();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            try {
                client.connect(new InetSocketAddress(pcIpInfor.ip, pcIpInfor.port), SOCKET_TIMEOUT);
                IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
                IOUtils.copy(client.getInputStream(), outBytes, 8192);
                dataReceived = new String(outBytes.toByteArray(), "UTF-8");
                Log.i("Send2PCThread", "指令: " + cmdStr);
                Log.i("Send2PCThread", "回复: " + dataReceived);
                if(dataReceived.length() > 0) {
                    try {
                        ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataReceived, ReceivedActionMessageFormat.class);
                        if(receivedMsg.getStatus() == OrderConst.success) {
                            if(receivedMsg.getData() != null)
                                parseMesgReceived(receivedMsg.getData());
                            reportResult(true);
                        } else {
                            reportResult(false);
                        }
                    } catch (Exception e) {
                        Log.i("Send2PCThread", "catch" + e.getMessage());
                        reportResult(false);
                    }
                } else reportResult(false);
            }catch (IOException e) {
                e.printStackTrace();
                reportResult(false);
            } finally {
                if (!client.isClosed()) {
                    IOUtils.closeQuietly(client);
                }
            }
        } else reportResult(false);

    }

    /**
     * <summary>
     *  创建请求指令字符串
     * </summary>
     * <returns>发送给PC的请求指令</returns>
     */
    private String createCmdStr() {
        String cmdStr = "";
        switch (typeName) {
            case OrderConst.sysAction_name:
                switch (commandType) {
                    case OrderConst.sysAction_getInfor_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCInforCommand());
                        break;
                    case OrderConst.sysAction_getScreenState_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCScreenStateCommand());
                        break;
                }
                break;
            case OrderConst.appAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getList_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCAppCommand());
                        break;
                    case OrderConst.appAction_miracstOpen_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createOpenPcAPPMiracastCommand(param.get("tvname"),
                                param.get("appname"), param.get("path")));
                        break;
                    case OrderConst.appAction_rdpOpen_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createOpenPcRdpAppCommand(param.get("appname"),
                                param.get("path")));
                        break;
                }
            }   break;
            case OrderConst.gameAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getList_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCGameCommand());
                        break;
                    case OrderConst.gameAction_open_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createOpenPcGameCommand(param.get("gamename"), param.get("path")));
                        break;
                }
            }   break;
            case OrderConst.videoAction_name:
            case OrderConst.imageAction_name:
            case OrderConst.audioAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getRecent_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCRecentListCommand(typeName));
                        break;
                    case OrderConst.mediaAction_getSets_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPCMediaSetsCommand(typeName));
                        break;
                    case OrderConst.appMediaAction_getList_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createGetPcMediaListCommand(path, typeName, isRoot));
                        break;
                    case OrderConst.mediaAction_play_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createOpenPcMediaCommand(typeName, param.get("filename"),
                                param.get("path"), param.get("tvname")));
                        break;
                    case OrderConst.mediaAction_addSet_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createAddPcMediaPlaySetCommand(typeName, param.get("setname")));
                        break;
                    case OrderConst.mediaAction_deleteSet_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createDeletePcMediaPlaySetCommand(typeName, param.get("setname")));
                        break;
                    case OrderConst.mediaAction_addFilesToSet_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createAddPcFilesToSetCommand(typeName, param.get("setname"), param.get("liststr")));
                        break;
                    case OrderConst.mediaAction_playSet_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createOpenPcMediaSetCommand(typeName, param.get("setname"), param.get("tvname")));
                        break;
                }
            }   break;
        }
        return cmdStr;
    }

    /**
     * <summary>
     *  解析PC返回的数据的data部分并设置相应的静态变量
     * </summary>
     */
    private void parseMesgReceived(String data) {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader((data)));
        reader.setLenient(true);
        switch (typeName) {
            case OrderConst.sysAction_name:
                switch (commandType) {
                    case OrderConst.sysAction_getInfor_command:
                        PCAppHelper.setPcInfor(JsonUitl.stringToBean(data, PCInforBean.class));
                        break;
                    case OrderConst.sysAction_getScreenState_command:
                        break;
                }
                break;
            case OrderConst.gameAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getList_command:
                        List<AppItem> list = gson.fromJson(reader, new TypeToken<List<AppItem>>(){}.getType());
                        PCAppHelper.setList(typeName, list);
                        break;
                    case OrderConst.gameAction_open_command:
                        break;
                }
            }   break;
            case OrderConst.appAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getList_command:
                        List<AppItem> list = gson.fromJson(reader, new TypeToken<List<AppItem>>(){}.getType());
                        PCAppHelper.setList(typeName, list);
                        break;
                    case OrderConst.appAction_miracstOpen_command:
                    case OrderConst.appAction_rdpOpen_command:
                        break;
                }
            }   break;
            case OrderConst.audioAction_name:
            case OrderConst.imageAction_name:
            case OrderConst.videoAction_name: {
                switch (commandType) {
                    case OrderConst.appMediaAction_getRecent_command:
                        List<MediaItem> recentFiles = gson.fromJson(reader, new TypeToken<List<MediaItem>>(){}.getType());
                        Collections.reverse(recentFiles);
                        MediafileHelper.setRecentFiles(recentFiles, typeName);
                        break;
                    case OrderConst.appMediaAction_getList_command:
                        List<MediaItem> mediaList = gson.fromJson(reader, new TypeToken<List<MediaItem>>(){}.getType());
                        List<MediaItem> files = new ArrayList<>();
                        List<MediaItem> folders = new ArrayList<>();
                        int size = mediaList.size();
                        for(int i = 0; i < size; ++i) {
                            if(mediaList.get(i).getType().equals("FOLDER")){
                                folders.add(mediaList.get(i));
                                Log.w("SendToPCThread",mediaList.get(i).getName());
                            }
                            else files.add(mediaList.get(i));
                        }
                        MediafileHelper.setMediaFiles(files, folders);
                        break;
                    case OrderConst.mediaAction_getSets_command:
                        Map<String, List<MediaItem>> mediaMap = gson.fromJson(reader, new TypeToken<Map<String, List<MediaItem>>>(){}.getType());
                        MediafileHelper.setMediaSets(mediaMap, typeName);
                        break;
                    case OrderConst.mediaAction_play_command:
                    case OrderConst.mediaAction_addSet_command:
                    case OrderConst.mediaAction_deleteSet_command:
                    case OrderConst.mediaAction_addFilesToSet_command:
                        break;
                }
            }   break;
        }
    }

    /**
     * <summary>
     *  发送相应的Handler消息
     * </summary>
     */
    private void reportResult(boolean result) {
        if (result) {
            switch (typeName) {
                case OrderConst.sysAction_name:
                    switch (commandType) {
                        case OrderConst.sysAction_getInfor_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCInfor_OK);
                            break;
                        case OrderConst.sysAction_getScreenState_command:
                            myhandler.sendEmptyMessage(OrderConst.PCScreenNotLocked);
                            break;
                    }
                    break;
                case OrderConst.appAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCApp_OK);
                            break;
                        case OrderConst.appAction_miracstOpen_command:
                        case OrderConst.appAction_rdpOpen_command:
                            myhandler.sendEmptyMessage(OrderConst.openPCApp_OK);
                            break;
                    }
                    break;
                case OrderConst.gameAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCGame_OK);
                            break;
                        case OrderConst.gameAction_open_command:
                            myhandler.sendEmptyMessage(OrderConst.openPCGame_OK);
                            break;
                    }
                    break;
                case OrderConst.videoAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getRecent_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCRecentVideo_OK);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_OK);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_OK);
                            break;
                    }
                    break;
                case OrderConst.audioAction_name:
                    switch (commandType) {
                        case OrderConst.mediaAction_playSet_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMediaSet_OK);
                            break;
                        case OrderConst.appMediaAction_getRecent_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCRecentAudio_OK);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_OK);
                            break;
                        case OrderConst.mediaAction_getSets_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCAudioSets_OK);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_OK);
                            break;
                        case OrderConst.mediaAction_addSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCSet_OK);
                            break;
                        case OrderConst.mediaAction_deleteSet_command:
                            myhandler.sendEmptyMessage(OrderConst.deletePCSet_OK);
                            break;
                        case OrderConst.mediaAction_addFilesToSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCFilesToSet_OK);
                            break;
                    }
                    break;
                case OrderConst.imageAction_name:
                    switch (commandType) {
                        case OrderConst.mediaAction_playSet_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMediaSet_OK);
                            break;
                        case OrderConst.mediaAction_getSets_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCImageSets_OK);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_OK);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_OK);
                            break;
                        case OrderConst.mediaAction_addSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCSet_OK);
                            break;
                        case OrderConst.mediaAction_deleteSet_command:
                            myhandler.sendEmptyMessage(OrderConst.deletePCSet_OK);
                            break;
                        case OrderConst.mediaAction_addFilesToSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCFilesToSet_OK);
                            break;
                    }
                    break;
            }
        } else {
            switch (typeName) {
                case OrderConst.sysAction_name:
                    switch (commandType) {
                        case OrderConst.sysAction_getInfor_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCInfor_Fail);
                            break;
                        case OrderConst.sysAction_getScreenState_command:
                            myhandler.sendEmptyMessage(OrderConst.PCScreenLocked);
                            break;
                    }
                    break;
                case OrderConst.appAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCApp_Fail);
                            break;
                        case OrderConst.appAction_miracstOpen_command:
                        case OrderConst.appAction_rdpOpen_command:
                            myhandler.sendEmptyMessage(OrderConst.openPCApp_Fail);
                            break;
                    }
                    break;
                case OrderConst.gameAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCGame_Fail);
                            break;
                        case OrderConst.gameAction_open_command:
                            myhandler.sendEmptyMessage(OrderConst.openPCGame_Fail);
                            break;
                    }
                    break;
                case OrderConst.videoAction_name:
                    switch (commandType) {
                        case OrderConst.appMediaAction_getRecent_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCRecentVideo_Fail);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_Fail);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_Fail);
                            break;
                    }
                    break;
                case OrderConst.audioAction_name:
                    switch (commandType) {
                        case OrderConst.mediaAction_playSet_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMediaSet_Fail);
                            break;
                        case OrderConst.appMediaAction_getRecent_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCRecentAudio_Fail);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_Fail);
                            break;
                        case OrderConst.mediaAction_getSets_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCAudioSets_Fail);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_Fail);
                            break;
                        case OrderConst.mediaAction_addSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCSet_Fail);
                            break;
                        case OrderConst.mediaAction_deleteSet_command:
                            myhandler.sendEmptyMessage(OrderConst.deletePCSet_Fail);
                            break;
                        case OrderConst.mediaAction_addFilesToSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCFilesToSet_Fail);
                            break;
                    }
                    break;
                case OrderConst.imageAction_name:
                    switch (commandType) {
                        case OrderConst.mediaAction_playSet_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMediaSet_Fail);
                            break;
                        case OrderConst.mediaAction_getSets_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCImageSets_Fail);
                            break;
                        case OrderConst.appMediaAction_getList_command:
                            myhandler.sendEmptyMessage(OrderConst.getPCMedia_Fail);
                            break;
                        case OrderConst.mediaAction_play_command:
                            myhandler.sendEmptyMessage(OrderConst.playPCMedia_Fail);
                            break;
                        case OrderConst.mediaAction_addSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCSet_Fail);
                            break;
                        case OrderConst.mediaAction_deleteSet_command:
                            myhandler.sendEmptyMessage(OrderConst.deletePCSet_Fail);
                            break;
                        case OrderConst.mediaAction_addFilesToSet_command:
                            myhandler.sendEmptyMessage(OrderConst.addPCFilesToSet_Fail);
                            break;
                    }
                    break;
            }
        }
    }
}
