package com.dkzy.areaparty.phone.fragment01.utils;

import android.os.Handler;
import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.utils_comman.Send2SpecificPCThread;
import com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread;

/**
 * Created by borispaul on 17-9-16.
 */
public class IdentityVerify {
    /**
     * <summary>
     *  验证当前选中PC
     *  <param name="handler">消息传递句柄</param>
     * </summary>
     */
    public static void identifyPC(Handler handler, String code, String IP, int port) {
        Log.w("IdentityVerify PC","code："+code+"  &  IP:"+IP+"  &  port"+port);
        new Send2SpecificPCThread(OrderConst.identityAction_name, OrderConst.identityAction_command, handler, IP, port, code).start();
    }

    /**
     * <summary>
     *  验证当前选中TV
     *  <param name="handler">消息传递句柄</param>
     * </summary>
     */
    public static void identifyTV(Handler handler, String code, String IP, int port) {
        Log.w("IdentityVerify TV","code："+code+"  &  IP:"+IP+"  &  port"+port);
        new Send2SpecificTVThread(OrderConst.identityAction_name, OrderConst.identityAction_command, handler, IP, port, code).start();
    }
}
