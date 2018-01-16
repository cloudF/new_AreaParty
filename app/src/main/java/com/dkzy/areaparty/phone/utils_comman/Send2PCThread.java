package com.dkzy.areaparty.phone.utils_comman;

import android.os.Handler;
import android.util.Log;

import com.dkzy.areaparty.phone.AESc;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment02.Model.MediaItem;
import com.dkzy.areaparty.phone.fragment02.searchContent.SearchContainer;
import com.dkzy.areaparty.phone.fragment02.utils.MediafileHelper;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.Model.PCInforBean;
import com.dkzy.areaparty.phone.fragment03.utils.PCAppHelper;
import com.dkzy.areaparty.phone.model_comman.PCCommandItem;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.context;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.selectedPCIP;
import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;


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
    public static String password = null;
    public static String pass = null;
    public static String pass1 = null;
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

    public Send2PCThread(String type, String commandType, String path, Handler myhandler) {
        this.typeName = type;
        this.path = path;
        this.commandType = commandType;
        this.myhandler = myhandler;
    }



    @Override
    public void run() {
        IPInforBean pcIpInfor = MyApplication.getSelectedPCIP();
        if(pcIpInfor != null && !pcIpInfor.ip.equals("")) {
            Log.i("Send2PCThread", "执行线程" );
            String cmdStr = createCmdStr();
            String dataReceived = "";
            Socket client = new Socket();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            try {
                password = new PreferenceUtil(context).read("PCMACS");
                HashMap<String, String>  PCMacs = MyApplication.parse(password);
                pass=PCMacs.get(selectedPCIP.mac);
                pass1=stringToMD5(pass);
                String cmdStr1 = AESc.EncryptAsDoNet(cmdStr,pass1.substring(0,8));
                client.connect(new InetSocketAddress(pcIpInfor.ip, pcIpInfor.port), SOCKET_TIMEOUT);
//                IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
//                IOUtils.copy(client.getInputStream(), outBytes, 8192);
//                dataReceived = new String(outBytes.toByteArray(), "UTF-8");
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                writer.write(cmdStr1);
                writer.newLine();
                writer.flush();
                dataReceived = reader.readLine();

                String decryptdata =AESc.DecryptDoNet(dataReceived,pass1.substring(0,8));

                Log.i("Send2PCThread", "指令: " + cmdStr1);
                Log.i("Send2PCThread", "回复: " + decryptdata);
                if(decryptdata.length() > 0) {
                    try {
                        ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(decryptdata, ReceivedActionMessageFormat.class);
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
            } catch (Exception e) {
                e.printStackTrace();
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
                    case OrderConst.gameAction_kill_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createkillPcGameCommand());
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
                    case OrderConst.mediaAction_playSet_command_BGM:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createPlayAsBGMCommand(typeName, param.get("setname"), param.get("tvname")));
                        break;
                    case OrderConst.mediaAction_DELETE_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createDeleteCommand(path,typeName));
                        break;
                    case OrderConst.mediaAction_playALL_command:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createPlayAllCommand(param.get("folder"),param.get("tvname"),param.get("t"),typeName));
                        break;
                }
            }
            break;
            case OrderConst.addPathToHttp_Name:
                switch(commandType){
                    case OrderConst.Media_Search_By_Key:
                        cmdStr = JsonUitl.objectToString(CommandUtil.createSearchMediaCommand(path));
                }
                break;
            case OrderConst.UTOrrent:
                cmdStr = JsonUitl.objectToString(CommandUtil.openUtorrent());
                break;
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
                    case OrderConst.mediaAction_DELETE_command:
                        break;
                    case OrderConst.mediaAction_play_command:
                    case OrderConst.mediaAction_addSet_command:
                    case OrderConst.mediaAction_deleteSet_command:
                    case OrderConst.mediaAction_addFilesToSet_command:
                        break;

                }
            }
            break;
            case OrderConst.addPathToHttp_Name:
                switch(commandType){
                    case OrderConst.Media_Search_By_Key:
                        List<MediaItem> mediaList = gson.fromJson(reader, new TypeToken<List<MediaItem>>(){}.getType());
                        for(MediaItem item : mediaList) {
                            switch (item.getType()){
                                case OrderConst.videoAction_name: SearchContainer.videoList_pc.add(item);break;
                                case OrderConst.audioAction_name: SearchContainer.audioList_pc.add(item);break;
                                case OrderConst.imageAction_name: SearchContainer.imageList_pc.add(item);break;
                                default:break;
                            }
                        }
                        Log.w("Media_Search_By_Key",SearchContainer.videoList_pc.size()+"search");
                        Log.w("Media_Search_By_Key",SearchContainer.audioList_pc.size()+"search");
                        Log.w("Media_Search_By_Key",SearchContainer.imageList_pc.size()+"search");
                        break;
                }
            break;
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_OK);
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_OK);
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_OK);
                            break;
                    }
                    break;
                case OrderConst.addPathToHttp_Name:
                    switch(commandType){
                        case OrderConst.Media_Search_By_Key:
                            myhandler.sendEmptyMessage(OrderConst.success);
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_Fail);
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_Fail);
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
                        case OrderConst.mediaAction_DELETE_command:
                            myhandler.sendEmptyMessage(OrderConst.mediaAction_DELETE_Fail);
                            break;
                    }
                    break;
                case OrderConst.addPathToHttp_Name:
                    switch(commandType){
                        case OrderConst.Media_Search_By_Key:
                            myhandler.sendEmptyMessage(OrderConst.failure);
                            break;
                    }
                    break;
            }
        }
    }
}
