package com.dkzy.areaparty.phone.fragment03.utils;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment02.vedioPlayControl;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.Model.TVInforBean;
import com.dkzy.areaparty.phone.model_comman.TVCommandItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.CommandUtil;
import com.dkzy.areaparty.phone.utils_comman.GetTvListThread;
import com.dkzy.areaparty.phone.utils_comman.Send2TVThread;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by borispaul on 17-6-28.
 */

public class TVAppHelper {
    public static ArrayList<AppItem> installedAppList = new ArrayList<>();
    public static ArrayList<AppItem> ownAppList = new ArrayList<>();
    private static List<String> mouseDevices = new ArrayList<>();
    private static TVInforBean tvInfor = new TVInforBean();

    public static void resetTotalInfors() {
        installedAppList.clear();
        ownAppList.clear();
        mouseDevices.clear();
        tvInfor = new TVInforBean();
    }

    public static void setTvInfor(TVInforBean infor) {
        tvInfor = infor;
    }

    public static void setMouseDevices(List<String> list) {
        mouseDevices.clear();
        mouseDevices.addAll(list);
    }

    public static void setAppList(String type, List<AppItem> appList) {
        switch (type) {
            case OrderConst.getTVOtherApps_firCommand:
                installedAppList.clear();
                installedAppList.addAll(appList);
                break;
            case OrderConst.getTVSYSApps_firCommand:
                ownAppList.clear();
                ownAppList.addAll(appList);
                break;
        }
    }

    public static void loadApps(Handler handler) {
        installedAppList.clear();
        ownAppList.clear();
        new GetTvListThread(OrderConst.getTVOtherApps_firCommand, handler).start();
        new GetTvListThread(OrderConst.getTVSYSApps_firCommand, handler).start();
    }

    public static void loadMouses(Handler handler) {
        mouseDevices.clear();
        new GetTvListThread(OrderConst.getTVMouses_firCommand, handler).start();
    }

    public static void loadTVInfor(Handler handler) {
        new GetTvListThread(OrderConst.getTVInfor_firCommand, handler).start();
    }

    public static void openApp(String packgeName) {
        String cmd = JsonUitl.objectToString(CommandUtil.createOpenTvAppCommand(packgeName));
        new Send2TVThread(cmd).start();
    }

    public static void prepareForPCGame(String pc_ip, String pc_mac) {
        if (TextUtils.isEmpty(pc_mac)) pc_mac = "2C:4D:54:EC:FB:95";
        String cmd = JsonUitl.objectToString(CommandUtil.createPrepareForPCGameCommand(pc_ip, pc_mac));
        new Send2TVThread(cmd).start();
    }

    public static void closeGameStream(String pc_ip, String pc_mac) {
        String cmd = JsonUitl.objectToString(CommandUtil.createCloseStreamPCGameCommand(pc_ip, pc_mac));
        new Send2TVThread(cmd).start();
    }

    public static void closeApp(String packgeName) {
        String cmd = JsonUitl.objectToString(CommandUtil.createCloseTvAppCommand(packgeName));
        new Send2TVThread(cmd).start();
    }

    public static void uninstallApp(String packgeName) {
        String cmd = JsonUitl.objectToString(CommandUtil.createUninstallTVAppCommand(packgeName));
        new Send2TVThread(cmd).start();
    }

    public static void openSettingPage() {
        String cmd = JsonUitl.objectToString(CommandUtil.createTVSettingPageCommand());
        new Send2TVThread(cmd).start();
    }

    public static void shutDownTV() {
        String cmd = JsonUitl.objectToString(CommandUtil.createShutdownTVCommand());
        new Send2TVThread(cmd).start();
    }

    public static void rebootTV() {
        String cmd = JsonUitl.objectToString(CommandUtil.createRebootTVCommand());
        new Send2TVThread(cmd).start();
    }


    public static void openTVRDP() {
        String cmd = JsonUitl.objectToString(CommandUtil.createOpenTvRdpCommand());
        new Send2TVThread(cmd).start();
    }

    public static void openTVAccessibility() {
        String cmd = JsonUitl.objectToString(CommandUtil.createOpenTvAccessibilityCommand());
        new Send2TVThread(cmd).start();
    }


    public static void openTVMiracast() {
        String cmd = JsonUitl.objectToString(CommandUtil.createOpenTvMiracastCommand());
        new Send2TVThread(cmd).start();
    }

    //by ervincm
    public static void vedioPlayControlVolumeUp() {
        String cmd = JsonUitl.objectToString(CommandUtil.createVolumeUpVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlVolumeDown() {
        String cmd = JsonUitl.objectToString(CommandUtil.createVolumeDownVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlFast() {
        String cmd = JsonUitl.objectToString(CommandUtil.createFastVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlRewind() {
        String cmd = JsonUitl.objectToString(CommandUtil.createRewindVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlPlayPause() {
        String cmd = JsonUitl.objectToString(CommandUtil.createPlayPauseVLCCommand());
        new Send2TVThread(cmd).start();
    }

    public static void vedioPlayControlPause() {
        String cmd = JsonUitl.objectToString(CommandUtil.createPauseVLCCommand());
        new Send2TVThread(cmd).start();
    }

    public static void vedioPlayControlPlay() {
        String cmd = JsonUitl.objectToString(CommandUtil.createPlayVLCCommand());
        new Send2TVThread(cmd).start();
    }

    public static void vedioPlayControlstop() {
        String cmd = JsonUitl.objectToString(CommandUtil.createStopVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlExit() {
        String cmd = JsonUitl.objectToString(CommandUtil.createExitVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void openMoonlight() {
        String cmd = JsonUitl.objectToString(CommandUtil.createOpenMoonlightCommand());
        new Send2TVThread(cmd).start();
    }

    public static void playAppointPosition2TV() {
        TVCommandItem tvCommandItem=CommandUtil.createAppointPlayPositionVLCCommand();

        tvCommandItem.setFifthCommand(String.valueOf(vedioPlayControl.seekBar.getProgress()));

        String cmd = JsonUitl.objectToString(tvCommandItem);
        new Send2TVThread(cmd).start();
    }

    /**
     * <summary>
     *  告知TV，当前连接PC
     *  <param name="handler">消息传递句柄</param>
     * </summary>
     */

    public static void currentPcInfo2TV() {


        String cmd = JsonUitl.objectToString(CommandUtil.createCurrentPcInfoCommand());
        Log.e("ervincm", MyApplication.getSelectedPCIP().getName());
        new Send2TVThread(cmd).start();
    }


    public static ArrayList<AppItem> getInstalledAppList() {
        return installedAppList;
    }

    public static ArrayList<AppItem> getOwnAppList() {
        return ownAppList;
    }

    public static List<String> getMouseDevices() {
        return mouseDevices;
    }

    public static TVInforBean getTvInfor() {
        return tvInfor;
    }

    public static void vedioPlayControlHideSubtitle() {
        String cmd = JsonUitl.objectToString(CommandUtil.createHideSubtitleVLCCommand());
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlLoadSubtitle(String url,String f) {
        String cmd = JsonUitl.objectToString(CommandUtil.createLoadSubtitleVLCCommand(url, f));
        new Send2TVThread(cmd).start();
    }
    public static void vedioPlayControlSubtitleBefore() {
        String cmd = JsonUitl.objectToString(CommandUtil.createSubtitleBeforeVLCCommand());
        new Send2TVThread(cmd).start();
    }

    public static void vedioPlayControlSubtitleDelay() {
        String cmd = JsonUitl.objectToString(CommandUtil.createSubtitleDelayVLCCommand());
        new Send2TVThread(cmd).start();
    }
}
