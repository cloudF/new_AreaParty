package com.dkzy.areaparty.phone.utils_comman;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment01.ui.ActionDialog_playPicList;
import com.dkzy.areaparty.phone.model_comman.PCCommandItem;
import com.dkzy.areaparty.phone.model_comman.TVCommandItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by borispaul on 2017/6/29.
 * 部分指令集
 */

public class CommandUtil {

    /**
     * <summary>
     *  构建获取验证TV的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createVerifyTVCommand(String code) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.identityAction_name);
        cmd.setSecondcommand(OrderConst.identityAction_command);
        cmd.setFourthCommand(code);
        return cmd;
    }
    /**
     * <summary>
     *  构建获取验证TV无障碍服务的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createCheckTvAccessibilityCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.CHECK_ACCESSIBILITY_ISOPEN_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建暂停VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createPauseVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Pause_secondCommand);
        return cmd;
    }


    /**
     * <summary>
     *  构建继续VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createPlayVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Play_secondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建暂停/继续切换VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createPlayPauseVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Play_Pause_secondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建停止VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createStopVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Stop_secondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建快进10SVLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createFastVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Fast_secondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建快退10sVLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createRewindVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Rewind_SecondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建音量+VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createVolumeUpVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Volume_Up_secondCommand);
        return cmd;
    }


    /**
     * <summary>
     *  构建停止VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createVolumeDownVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Volume_Down_secondCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建退出VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createExitVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Exit_SecondCommand);
        return cmd;
    }
    public static TVCommandItem createOpenMoonlightCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_openMoonLightCommand);
        return cmd;
    }

    /**
     * <summary>
     *  构建按进度条播放的VLC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createAppointPlayPositionVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Appoint_Play_Position_secondCommand);
        return cmd;
    }



    /**
     * <summary>
     *  构建获取TV信息的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createGetTvInforCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.getTVInfor_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建获取TV连接的鼠标的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createGetTvMouseDevicesCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.getTVMouses_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建告知TV，当前连接PC的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createCurrentPcInfoCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.getPCInfor_firCommand);
        cmd.setSecondcommand(MyApplication.getSelectedPCIP().getName());
        cmd.setFourthCommand(MyApplication.getSelectedPCIP().getIp());
        cmd.setFifthCommand(String.valueOf(MyApplication.getSelectedPCIP().getPort()));
        cmd.setSevencommand(MyApplication.getSelectedPCIP().mac);
        return cmd;
    }


    /**
     * <summary>
     *  构建获取TV系统自带应用的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createGetTvSYSAppCommand(){
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.getTVSYSApps_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建获取TV上用户自己安装应用的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createGetTvOtherAppCommand(){
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.getTVOtherApps_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建打开TV上指定应用的指令
     * </summary>
     * <param name="appPackage">指定应用对应的包名</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createOpenTvAppCommand(String appPackage){
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.openTVApp_firCommand);
        cmd.setSecondcommand(appPackage);
        return cmd;
    }

    /**
     * <summary>
     *  构建关闭TV上指定应用的指令
     * </summary>
     * <param name="appPackage">指定应用对应的包名</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createCloseTvAppCommand(String appPackage) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.closeTVApp_firCommand);
        cmd.setSecondcommand(appPackage);
        return cmd;
    }

    /**
     * <summary>
     *  构建卸载TV上指定应用的指令
     * </summary>
     * <param name="appPackage">指定应用对应的包名</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createUninstallTVAppCommand(String appPackage) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.uninstallTVApp_firCommand);
        cmd.setSecondcommand(appPackage);
        return cmd;
    }

    /**
     * <summary>
     *  构建关闭TV的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createShutdownTVCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.shutdownTV_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建重启TV的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createRebootTVCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.rebootTV_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建打开TV设置页面的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createTVSettingPageCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.openSettingTV_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建打开TV上RDP模式的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createOpenTvRdpCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.openTVRdp_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建打开TV上ACCESSIBILITY的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createOpenTvAccessibilityCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.openTVAccessibility_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建打开TV上MIRACAST模式的指令
     * </summary>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createOpenTvMiracastCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.openTVMiracast_firCommand);
        cmd.setSecondcommand("");
        return cmd;
    }

    /**
     * <summary>
     *  构建在TV上打开指定URL的流媒体的指令
     * </summary>
     * <param name="url">流媒体资源定位符</param>
     * <param name="fileName">流媒体文件名称</param>
     * <param name="fileType">流媒体文件类别(只能是video、audio、image)</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createPlayUrlFileOnTVCommand(String url, String fileName, String fileType) {
        TVCommandItem cmd = new TVCommandItem();
        List<String> list = new ArrayList<>();
        list.add(url);
        cmd.setFirstcommand(OrderConst.dlnaCastToTV_Command);
        cmd.setFifthCommand(fileType);
        cmd.setSixthcommand(list);
        return cmd;
    }
    public static TVCommandItem createPlayUrlFileOnTVCommand(List<String> urls, String fileName, String fileType) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.dlnaCastToTV_Command);
        cmd.setSecondcommand(""+ ActionDialog_playPicList.t);
        cmd.setFifthCommand(fileType);
        cmd.setSixthcommand(urls);
        return cmd;
    }
    public static TVCommandItem createPlayBGMOnTVCommand(List<String> urls, String fileName, String fileType) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_BGM_secondCommand);
        cmd.setFifthCommand(fileType);
        cmd.setSixthcommand(urls);
        return cmd;
    }

    /**
     * <summary>
     *  构建在TV上准备接受PC游戏的指令(配对并开始串流)
     * </summary>
     * <param name="url">流媒体资源定位符</param>
     * <param name="fileName">流媒体文件名称</param>
     * <param name="fileType">流媒体文件类别(只能是video、audio、image)</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createPrepareForPCGameCommand(String pc_ip, String pc_mac) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.createPairAndStreamWithPC_firCommand);
        cmd.setSecondcommand(pc_ip);
        cmd.setFourthCommand(pc_mac);
        return cmd;
    }

    /**
     * <summary>
     *  构建在TV上准备接受PC游戏的指令(配对并开始串流)
     * </summary>
     * <param name="url">流媒体资源定位符</param>
     * <param name="fileName">流媒体文件名称</param>
     * <param name="fileType">流媒体文件类别(只能是video、audio、image)</param>
     * <returns>TV指令</returns>
     */
    public static TVCommandItem createCloseStreamPCGameCommand(String pc_ip, String pc_mac) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.quitStreamWithPC_firCommand);
        cmd.setSecondcommand(pc_ip);
        cmd.setFourthCommand(pc_mac);
        return cmd;
    }

    /**
     * <summary>
     *  构建获取验证PC的指令
     * </summary>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createVerifyPCCommand(String code) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.identityAction_name);
        cmd.setCommand(OrderConst.identityAction_command);
        cmd.param = new HashMap<>();
        cmd.param.put("code", code);
        return cmd;
    }

    /**
     * <summary>
     *  构建获取PC应用的指令
     * </summary>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCAppCommand() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.appAction_name);
        cmd.setCommand(OrderConst.appMediaAction_getList_command);
        cmd.param = new HashMap<>();
        return cmd;
    }

    /**
     * <summary>
     *  构建获取PC游戏的指令
     * </summary>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCGameCommand() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.gameAction_name);
        cmd.setCommand(OrderConst.appMediaAction_getList_command);
        cmd.param = new HashMap<>();
        return cmd;
    }

    /**
     * <summary>
     *  构建获取PC系统信息的指令
     * </summary>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCInforCommand() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.sysAction_name);
        cmd.setCommand(OrderConst.sysAction_getInfor_command);
        cmd.param = new HashMap<>();
        return cmd;
    }
    public static PCCommandItem openUtorrent() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.UTOrrent);
        return cmd;
    }

    /**
     * <summary>
     *  构建获取PC是否锁屏的指令
     * </summary>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCScreenStateCommand() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.sysAction_name);
        cmd.setCommand(OrderConst.sysAction_getScreenState_command);
        cmd.param = new HashMap<>();
        return cmd;
    }

    /**
     * <summary>
     *  构建获取指定路径下媒体文件列表的指令
     * </summary>
     * <param name="path">路径信息</param>
     * <param name="type">文件类别(VIDEO、AUDIO、IMAGE)</param>
     * <param name="root">是否是媒体库根目录</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPcMediaListCommand(String path, String type, Boolean root) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.appMediaAction_getList_command);
        Map<String, String> params = new HashMap<>();
        if (root) {
            params.put("folder", "root");
        }else {
            params.put("folder", path);
        }
        cmd.param = params;
        return cmd;
    }

    public static PCCommandItem createDeleteCommand(String path, String type) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_DELETE_command);
        Map<String, String> params = new HashMap<>();
        params.put("folder", path);
        cmd.param = params;
        return cmd;
    }

    public static PCCommandItem createSearchMediaCommand(String key) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.addPathToHttp_Name);
        cmd.setCommand(OrderConst.Media_Search_By_Key);
        Map<String, String> params = new HashMap<>();
        params.put("key", key);
        cmd.param = params;
        return cmd;
    }

    public static PCCommandItem createPlayAllCommand(String path,String tvname ,String t,String type) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_playALL_command);
        Map<String, String> params = new HashMap<>();
        params.put("folder", path);
        params.put("tvname", tvname);
        params.put("t",t);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  构建获取最近播放媒体文件列表的指令
     * </summary>
     * <param name="typeName">媒体文件类别(AUDIO、VIDEO)</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCRecentListCommand(String typeName) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(typeName);
        cmd.setCommand(OrderConst.appMediaAction_getRecent_command);
        return cmd;
    }

    /**
     * <summary>
     *  构建获取图片列表或音频列表的指令
     * </summary>
     * <param name="typeName">媒体文件类别(AUDIO、IMAGE)</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createGetPCMediaSetsCommand(String typeName) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(typeName);
        cmd.setCommand(OrderConst.mediaAction_getSets_command);
        return cmd;
    }

    /**
     * <summary>
     *  以Miracast模式打开指定app的指令
     * </summary>
     * <param name="tvname">tv名称</param>
     * <param name="appname">应用名称</param>
     * <param name="path">应用路径</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createOpenPcAPPMiracastCommand(String tvname, String appname, String path){
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.appAction_name);
        cmd.setCommand(OrderConst.appAction_miracstOpen_command);
        Map<String, String> params = new HashMap<>();
        params.put("tvname", tvname);
        params.put("appname", appname);
        params.put("path", path);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  以rdp模式打开PC上指定的app的指令
     * </summary>
     * <param name="appname">应用名称</param>
     * <param name="path">应用路径</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createOpenPcRdpAppCommand(String appname, String path) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.appAction_name);
        cmd.setCommand(OrderConst.appAction_rdpOpen_command);
        Map<String, String> params = new HashMap<>();
        params.put("appname", appname);
        params.put("path", path);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  打开PC上指定的游戏的指令
     * </summary>
     * <param name="gamename">游戏名称</param>
     * <param name="path">游戏路径</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createOpenPcGameCommand(String gamename, String path) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.gameAction_name);
        cmd.setCommand(OrderConst.gameAction_open_command);
        Map<String, String> params = new HashMap<>();
        params.put("gamename", gamename);
        params.put("path", path);
        cmd.param = params;
        return cmd;
    }
    /**
     * <summary>
     *  关闭pc上的游戏
     * </summary>
     * <param name="gamename">游戏名称</param>
     * <param name="path">游戏路径</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createkillPcGameCommand() {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(OrderConst.gameAction_name);
        cmd.setCommand(OrderConst.gameAction_kill_command);
        Map<String, String> params = new HashMap<>();
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  打开PC上指定的媒体文件到指定TV的指令
     * </summary>
     * <param name="type">媒体文件类别(AUDIO、VIDEO、IMAGE)</param>
     * <param name="filename">媒体文件名称</param>
     * <param name="path">媒体文件路径信息</param>
     * <param name="tvname">tv名称</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createOpenPcMediaCommand(String type, String filename, String path, String tvname) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_play_command);
        Map<String, String> params = new HashMap<>();
        params.put("filename", filename);
        params.put("path", path);//
        params.put("tvname", tvname);//
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  打开PC上指定的媒体文件到指定TV的指令
     * </summary>
     * <param name="type">媒体文件类别(AUDIO、VIDEO、IMAGE)</param>
     * <param name="filename">媒体文件名称</param>
     * <param name="path">媒体文件路径信息</param>
     * <param name="tvname">tv名称</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createOpenPcMediaSetCommand(String type, String setname, String tvname) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_playSet_command);
        Map<String, String> params = new HashMap<>();
        params.put("setname", setname);
        params.put("tvname", tvname);
        cmd.param = params;
        return cmd;
    }
    public static PCCommandItem createPlayAsBGMCommand(String type, String setname, String tvname) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_playSet_command_BGM);
        Map<String, String> params = new HashMap<>();
        params.put("setname", setname);
        params.put("tvname", tvname);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  向PC上增加播放列表的指令
     * </summary>
     * <param name="type">媒体文件类别(AUDIO、IMAGE)</param>
     * <param name="name">播放列表名称</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createAddPcMediaPlaySetCommand(String type, String setname) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_addSet_command);
        Map<String, String> params = new HashMap<>();
        params.put("setname", setname);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  向PC上删除指定播放列表的指令
     * </summary>
     * <param name="type">媒体文件类别(AUDIO、IMAGE)</param>
     * <param name="name">播放列表名称</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createDeletePcMediaPlaySetCommand(String type, String setname) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_deleteSet_command);
        Map<String, String> params = new HashMap<>();
        params.put("setname", setname);
        cmd.param = params;
        return cmd;
    }

    /**
     * <summary>
     *  向PC上增加媒体文件列表到指定播放列表的指令
     * </summary>
     * <param name="type">媒体文件类别(AUDIO、IMAGE)</param>
     * <param name="name">播放列表名称</param>
     * <returns>PC指令</returns>
     */
    public static PCCommandItem createAddPcFilesToSetCommand(String type, String setname, String liststr) {
        PCCommandItem cmd = new PCCommandItem();
        cmd.setName(type);
        cmd.setCommand(OrderConst.mediaAction_addFilesToSet_command);
        Map<String, String> params = new HashMap<>();
        params.put("setname", setname);
        params.put("liststr", liststr);
        cmd.param = params;
        return cmd;
    }

    public static TVCommandItem createLoadSubtitleVLCCommand(String url,String f) {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_LoadSubtitle_SecondCommand);
        cmd.setFourthCommand(url);
        cmd.setFifthCommand(f);
        return cmd;
    }


    public static TVCommandItem createHideSubtitleVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_HideSubtitle_SecondCommand);
        return cmd;
    }

    public static TVCommandItem createSubtitleBeforeVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_Subtitle_Before_SecondCommand);
        return cmd;
    }
    public static TVCommandItem createSubtitleDelayVLCCommand() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.VLCAction_firCommand);
        cmd.setSecondcommand(OrderConst.VLCAction_SubtitleDelay_SecondCommand);
        return cmd;
    }

    public static TVCommandItem closeRdp() {
        TVCommandItem cmd = new TVCommandItem();
        cmd.setFirstcommand(OrderConst.CLOSERDP);
        return cmd;
    }


}
