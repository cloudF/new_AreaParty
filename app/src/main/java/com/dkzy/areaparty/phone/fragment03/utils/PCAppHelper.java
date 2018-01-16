package com.dkzy.areaparty.phone.fragment03.utils;

import android.os.Handler;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment01.prepareDataForFragment;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.Model.PCInforBean;
import com.dkzy.areaparty.phone.utils_comman.Send2PCThread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by borispaul on 17-6-28.
 */

public class PCAppHelper {
    public static final int NONEMODE = 0;
    public static final int RDPMODE = 1;
    public static final int MIRACAST = 2;
    public static ArrayList<AppItem> appList = new ArrayList<>();
    public static ArrayList<AppItem> gameList = new ArrayList<>();
    private static PCInforBean pcInfor = new PCInforBean();
    private static int currentMode = NONEMODE;

    public static void resetTotalInfors() {
        appList.clear();
        gameList.clear();
        pcInfor = new PCInforBean();
    }

    public static void setPcInfor(PCInforBean infor) {
        pcInfor = infor;
    }

    public static void setList(String type, List<AppItem> list) {
        switch (type) {
            case OrderConst.gameAction_name:
                gameList.clear();
                gameList.addAll(list);
                break;
            case OrderConst.appAction_name:
                appList.clear();
                appList.addAll(list);
                break;
        }
    }

    public static void loadList(Handler handler) {
        appList.clear();
        gameList.clear();
        new Send2PCThread(OrderConst.appAction_name, OrderConst.appMediaAction_getList_command, handler).start();
        new Send2PCThread(OrderConst.gameAction_name, OrderConst.appMediaAction_getList_command, handler).start();
    }

    public static void loadPCInfor(Handler handler) {
        new Send2PCThread(OrderConst.sysAction_name, OrderConst.sysAction_getInfor_command, handler).start();
    }

    public static void shutdownPC() {
        new Thread() {
            @Override
            public void run() {
                prepareDataForFragment.getActionStateData(OrderConst.computerAction_name, OrderConst.computerAction_shutdown_command, "");
            }
        }.start();
    }

    public static void rebootPC() {
        new Thread() {
            @Override
            public void run() {
                prepareDataForFragment.getActionStateData(OrderConst.computerAction_name, OrderConst.computerAction_reboot_command, "");
            }
        }.start();
    }

    public static void openApp_Rdp(String path, String appname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("path", path);
        param.put("appname", appname);
        new Send2PCThread(OrderConst.appAction_name, OrderConst.appAction_rdpOpen_command, param, myhandler).start();
    }

    public static void openApp_Miracast(String path, String appname, String tvname, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("path", path);
        param.put("appname", appname);
        param.put("tvname", tvname);
        new Send2PCThread(OrderConst.appAction_name, OrderConst.appAction_miracstOpen_command, param, myhandler).start();
    }

    public static void openPCGame(String path, String gamename, Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        param.put("path", path);
        param.put("gamename", gamename);
        new Send2PCThread(OrderConst.gameAction_name, OrderConst.gameAction_open_command, param, myhandler).start();
    }
    public static void killPCGame(Handler myhandler) {
        Map<String, String> param = new HashMap<>();
        new Send2PCThread(OrderConst.gameAction_name, OrderConst.gameAction_kill_command, param, myhandler).start();
    }

    public static void isPCScreenLocked(Handler myhandler) {
        new Send2PCThread(OrderConst.sysAction_name, OrderConst.sysAction_getScreenState_command, myhandler).start();
    }


    public static ArrayList<AppItem> getAppList() {
        return appList;
    }

    public static ArrayList<AppItem> getGameList() {
        return gameList;
    }

    public static PCInforBean getPcInfor() {
        return pcInfor;
    }

    public static int getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(int currentMode) {
        PCAppHelper.currentMode = currentMode;
    }
}
