package com.dkzy.areaparty.phone.utils_comman;

import android.os.Handler;
import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.fragment03.Model.AppItem;
import com.dkzy.areaparty.phone.fragment03.Model.TVInforBean;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.newAES;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.context;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.selectedTVIP;
import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;


/**
 * Created by borispaul on 2017/6/23.
 * 获取TV的应用、鼠标列表、信息的线程
 */

public class GetTvListThread extends Thread {
    private static final int SOCKET_TIMEOUT = 5000;
    private final String tag =this.getClass().getSimpleName();

    private String type;   // "GET_TV_INSTALLEDAPPS, GET_TV_SYSAPPS, GET_TV_MOUSES, GET_TV_INFOR"
    private Handler handler;
    public static String password = null;
    public static String pass = null;
    public static String pass1 = null;
    public GetTvListThread(String type, Handler handler) {
        this.type = type;
        this.handler = handler;
    }

    @Override
    public void run() {
        IPInforBean tvIpInfor = MyApplication.getSelectedTVIP();
        if(tvIpInfor != null && !tvIpInfor.ip.equals("")) {
            String cmdStr = createCmdStr();
            String dataReceived = "";
            Socket client = new Socket();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            try {
                password = new PreferenceUtil(context).read("TVMACS");
                HashMap<String, String> TVMacs = MyApplication.parse(password);
                pass=TVMacs.get(selectedTVIP.mac);
                pass1=stringToMD5(pass);

                String cmdStr1 = newAES.encrypt(cmdStr,pass1.getBytes());

                client.connect(new InetSocketAddress(tvIpInfor.ip, tvIpInfor.port), SOCKET_TIMEOUT);


                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                writer.write(cmdStr1);
                writer.newLine();
                writer.flush();
                dataReceived = reader.readLine();

                String decryptdata = newAES.decrypt(dataReceived,pass1.getBytes());

                Log.i("GetTvListThread", "指令: " + cmdStr1);
                Log.i("GetTvListThread", "回复: " + decryptdata);
                if(decryptdata.length() > 0) {
                    parseMesgReceived(decryptdata);
                    reportResult(true);
                } else reportResult(false);
            } catch (Exception e) {
                e.printStackTrace();
                reportResult(false);
            }finally {
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
     * <returns>发送给TV的请求数据指令</returns>
     */
    private String createCmdStr() {
        String cmdStr = "";
        switch (type) {
            case OrderConst.getTVSYSApps_firCommand:
                cmdStr = JsonUitl.objectToString(CommandUtil.createGetTvSYSAppCommand()) + "\n";
                break;
            case OrderConst.getTVOtherApps_firCommand:
                cmdStr = JsonUitl.objectToString(CommandUtil.createGetTvOtherAppCommand()) + "\n";
                break;
            case OrderConst.getTVMouses_firCommand:
                cmdStr = JsonUitl.objectToString(CommandUtil.createGetTvMouseDevicesCommand()) + "\n";
                break;
            case OrderConst.getTVInfor_firCommand:
                cmdStr = JsonUitl.objectToString(CommandUtil.createGetTvInforCommand()) + "\n";
                break;
        }
        return cmdStr;
    }

    /**
     * <summary>
     *  解析TV返回的数据并设置相应的静态变量
     * </summary>
     */
    private void parseMesgReceived(String dataReceived) {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader((dataReceived)));
        reader.setLenient(true);
        switch (type) {
            case OrderConst.getTVOtherApps_firCommand:
            case OrderConst.getTVSYSApps_firCommand: {
                try {
                    List<AppItem> list = gson.fromJson(reader, new TypeToken<List<AppItem>>(){}.getType());
                    TVAppHelper.setAppList(type, list);
                } catch (Exception e) {
                    Log.e("GetTvListThread", "逆序列" + type + "失败");
                }
            }   break;
            case OrderConst.getTVMouses_firCommand: {
                try {
                    List<String> list = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());
                    TVAppHelper.setMouseDevices(list);
                } catch (Exception e) {
                    Log.e("GetTvListThread", "逆序列" + type + "失败");
                }
            }   break;
            case OrderConst.getTVInfor_firCommand: {
                try {
                    TVInforBean inforBean = JsonUitl.stringToBean(dataReceived, TVInforBean.class);
                    TVAppHelper.setTvInfor(inforBean);
                } catch (Exception e) {
                    Log.e("GetTvListThread", "逆序列" + type + "失败");
                }
            }   break;
        }
    }

    /**
     * <summary>
     *  发送相应的Handler消息
     * </summary>
     */
    public void reportResult(boolean result) {
        if(result) {
            switch (type) {
                case OrderConst.getTVOtherApps_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVOtherApp_OK);
                    break;
                case OrderConst.getTVSYSApps_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVSYSApp_OK);
                    break;
                case OrderConst.getTVMouses_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVMouse_OK);
                    break;
                case OrderConst.getTVInfor_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVInfor_OK);
                    break;
            }
        } else {
            switch (type) {
                case OrderConst.getTVOtherApps_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVOtherApp_Fail);
                    break;
                case OrderConst.getTVSYSApps_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVSYSApp_Fail);
                    break;
                case OrderConst.getTVMouses_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVMouse_Fail);
                    break;
                case OrderConst.getTVInfor_firCommand:
                    handler.sendEmptyMessage(OrderConst.getTVInfor_Fail);
                    break;
            }
        }
    }

}
