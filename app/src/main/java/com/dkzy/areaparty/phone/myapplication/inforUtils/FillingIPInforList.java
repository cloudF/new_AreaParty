package com.dkzy.areaparty.phone.myapplication.inforUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforMessageBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/2 16:54
 */

public class FillingIPInforList {

    private static int intervalTime1;
    private static int intervalTime2;
    private static ArrayList<IPInforBean> TVList = new ArrayList<>();
    private static ArrayList<IPInforBean> PCList_B = new ArrayList<>();
    private static ArrayList<IPInforBean> PCList_Y = new ArrayList<>();
    private static String data;
    private static String ipStr = "";      // 该手机在wifi下的IP
    private static String wifiName = "";   // 当前wifi名称
    private static String ipbroadCastStr;
    private static Thread threadBroadCast;
    private static Thread threadReceiveMessage;
    private static Thread threadSendStaticMsgToServer;
    private static boolean closeSignal = false;
    private static boolean isNewPC_YList = false;
    private static boolean isNewTVList = false;

    public static Thread getThreadBroadCast() {
        return threadBroadCast;
    }

    public static Thread getThreadReceiveMessage() {
        return threadReceiveMessage;
    }

    public static String getWifiName() {
        return wifiName;
    }

    public static String getIpStr() {
        return ipStr;
    }

    public static ArrayList<IPInforBean> getTVList() {
        return TVList;
    }

    public static ArrayList<IPInforBean> getPCList_B() {
        return PCList_B;
    }

    public static ArrayList<IPInforBean> getPCList_Y() {
        return PCList_Y;
    }

    public static void setCloseSignal(boolean closeSignal) {
        FillingIPInforList.closeSignal = closeSignal;
    }
    public static boolean getCloseSingnal(){
        return closeSignal;
    }

    public static void startBroadCastAndListen(int time1, int time2) {
        FillingIPInforList.intervalTime1 = time1;
        FillingIPInforList.intervalTime2 = time2;
        initParam();
        threadBroadCast.start();
        threadReceiveMessage.start();
    }

    private static void initParam() {
        TVList.clear();
        PCList_Y.clear();
        PCList_B.clear();
        IPInforMessageBean message = new IPInforMessageBean();
        @SuppressLint("WifiManagerLeak") WifiManager wifiMgr = (WifiManager) MyApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        wifiName = wifiInfo.getSSID();
        int ip = wifiInfo.getIpAddress();
        int broadCastIP = ip | 0xFF000000;
        ipStr = Formatter.formatIpAddress(ip);
        ipbroadCastStr = Formatter.formatIpAddress(broadCastIP);
        List<IPInforBean> temp = new ArrayList<>();
        temp.add(new IPInforBean(ipStr, IPAddressConst.PHONEBROADCASTRECEIVEPORT_B, "", MyApplication.getInstance().getLaunchTimeId()));
        message.source = OrderConst.ip_phone_source;
        message.type = OrderConst.ip_default_type;
        message.param = temp;
        data = JsonUitl.objectToString(message);

        threadBroadCast = new Thread() {
            @Override
            public void run() { runBroadCast(); }
        };

        threadReceiveMessage = new Thread() {
            @Override
            public void run() { runReceiveMessage(); }
        };

        threadSendStaticMsgToServer = new Thread() {
            @Override
            public void run() {
                runSendStatistic2Server();
            }
        };
    }

    public static Thread getStatisticThread() {
        return threadSendStaticMsgToServer;
    }

    private static void runBroadCast() {
        while(!closeSignal) {
            int times = 10;
            while(times > 0 && (!closeSignal)) {
                DatagramSocket socket;
                try {
                    InetAddress server = InetAddress.getByName(ipbroadCastStr);
                    socket = new DatagramSocket();
                    Log.e("pushTV", "广播消息" + data);
                    Log.w("pushTV", ipbroadCastStr);
                    DatagramPacket outputPacket = new DatagramPacket(data.getBytes(), data.length(), server, IPAddressConst.PHONEBROADCASTSEND_B);
                    socket.send(outputPacket);
                } catch (IOException e) { e.printStackTrace(); }

                times--;
                try { Thread.sleep(intervalTime1);}
                catch (InterruptedException e) { e.printStackTrace(); }
            }

            try { Thread.sleep(intervalTime2); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private static void runSendStatistic2Server() {
        String type = "app";
        String userId = Login.userId;
        String id = getAdresseMAC(MyApplication.getContext());
        String mac = id;
        long time = System.currentTimeMillis();
        List<StatisticsMsgBean> pcMsg = new ArrayList<>(),
                tvMsg = new ArrayList<>();

        try { Thread.sleep(10000);}
        catch (InterruptedException e) { e.printStackTrace(); }
        while(!closeSignal) {
            tvMsg.clear();
            pcMsg.clear();
            for (IPInforBean temp : TVList) {
                tvMsg.add(new StatisticsMsgBean("tv", temp.name, temp.mac, time));
            }
            for (IPInforBean temp : PCList_Y) {
                pcMsg.add(new StatisticsMsgBean("pc", temp.name, temp.mac, time));
            }
            String allMsgStr = JsonUitl.objectToString(new AllStatisticsMsgBean(type, userId, id, mac, time, pcMsg, tvMsg));
            try {
                InetAddress address = InetAddress.getByName(IPAddressConst.statisticServer_ip);
                DatagramPacket datagramPacket = new DatagramPacket(allMsgStr.getBytes(),
                        allMsgStr.length(), address, IPAddressConst.statisticServer_port);
                DatagramSocket socket = new DatagramSocket();
                socket.send(datagramPacket);
                Log.e("pushServer", "广播消息" + allMsgStr);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try { Thread.sleep(20 * 60000);}
            catch (InterruptedException e) { e.printStackTrace(); }
        }



    }

    private static void runReceiveMessage() {
        ServerSocket socket;
        Socket socket1;
        IPInforMessageBean receivedObject;
        char[] rev = new char[1000];
        int len;
        try {
            socket = new ServerSocket(IPAddressConst.PHONEBROADCASTRECEIVEPORT_B);
            while(!closeSignal) {
                try {
                    socket1 = socket.accept();
                    BufferedReader temp = new BufferedReader(new InputStreamReader(socket1.getInputStream(), "utf-8"));
                    if((len = temp.read(rev)) > 0) {
                        String tempMessageStr = new String(rev, 0, len);
                        try{
                            Log.e("pushTV", "接收到的消息" + tempMessageStr);
                            receivedObject = JsonUitl.stringToBean(tempMessageStr, IPInforMessageBean.class);
                            parse(receivedObject);
                            sendIPInfor();
                        }catch (Exception e){}
                    }
                    socket1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            socket.close();
            //socket1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * <summary>
     *  解析得到IP信息并添加到对应List中，同时设置标识信息
     * </summary>
     * <param name="object">IPInforMessageBean</param>
     */
    private static void parse(IPInforMessageBean object) {
        if(object != null) {
            if((object.source).equals(OrderConst.ip_TV_source)) {
                for (IPInforBean IPinfor : object.param) {
                    if(!isIPInforContained(IPinfor, TVList)) {
                        IPinfor.nickName = IPinfor.name;
                        try {
                            String name = new PreferenceUtil(MyApplication.getContext()).read(IPinfor.mac);
                            if(name != null && !name.equals(""))
                                IPinfor.nickName = name;
                        } catch (Exception e) {
                            Log.e("FillingIPInforList", "无当前MAC对应名称");
                        }
                        if(MyApplication.getSelectedTVIP() != null &&
                                MyApplication.getSelectedTVIP().mac.equals(IPinfor.mac) &&
                                !MyApplication.getSelectedTVIP().name.equals(IPinfor.name)) {
                            IPInforBean newSelectedTV = new IPInforBean(IPinfor.ip, IPinfor.port, IPinfor.function, IPinfor.launch_time_id);
                            newSelectedTV.name = IPinfor.name;
                            newSelectedTV.nickName = MyApplication.getSelectedTVIP().nickName;
                            MyApplication.setSelectedTVIP(newSelectedTV);
                            new PreferenceUtil(MyApplication.getContext()).write("lastChosenTV", JsonUitl.objectToString(newSelectedTV));
                        }
                        TVList.add(IPinfor);
                        isNewTVList = true;
                    }
                }
            } else if((object.source).equals(OrderConst.ip_PC_B_source)) {
                for (IPInforBean IPinfor : object.param) {
                    if(!isIPInforContained(IPinfor, PCList_B)) {
                        PCList_B.add(IPinfor);
                    }
                }

            } else if((object.source).equals(OrderConst.ip_PC_Y_source)) {
                for (IPInforBean IPinfor : object.param) {
                    if(!isIPInforContained(IPinfor, PCList_Y)) {
                        IPinfor.nickName = IPinfor.name;
                        try {
                            String name = new PreferenceUtil(MyApplication.getContext()).read(IPinfor.mac);
                            if(name != null && !name.equals(""))
                                IPinfor.nickName = name;
                        } catch (Exception e) {
                            Log.e("FillingIPInforList", "无当前MAC对应名称");
                        }
                        PCList_Y.add(IPinfor);
                        isNewPC_YList = true;
                    }
                }
            }
        }
    }

    private static void sendIPInfor() {
        if((isNewTVList || isNewPC_YList) && TVList.size() > 0 && PCList_Y.size() > 0) {
            IPInforMessageBean message = new IPInforMessageBean();
            message.source = OrderConst.ip_phone_source;
            message.type = OrderConst.ip_default_type;
            message.param = TVList;
            String TVMessageStr = JsonUitl.objectToString(message);
            message.param = PCList_Y;
            String PC_YMessageStr = JsonUitl.objectToString(message);

            for(IPInforBean IPInfor : TVList) {
                try {
                    Socket actionSocket = new Socket(IPInfor.ip, IPInfor.port);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                    bufferedWriter.write(PC_YMessageStr);
                    bufferedWriter.flush();
                    actionSocket.close();
                    bufferedWriter.close();
                    Log.e("pushTV", "发送TV信息:"+PC_YMessageStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for(IPInforBean IPInfor : PCList_Y) {
                try {
                    Socket actionSocket = new Socket(IPInfor.ip, IPInfor.port);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                    bufferedWriter.write(TVMessageStr);
                    bufferedWriter.flush();
                    actionSocket.close();
                    bufferedWriter.close();
                    Log.e("pushTV", "发送PC信息:"+TVMessageStr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            isNewTVList = false;
            isNewPC_YList = false;
        }
    }

    private static boolean isIPInforContained(IPInforBean IPinfor, List<IPInforBean> list) {
        for(int i = 0; i < list.size(); i++) {
            IPInforBean temp = list.get(i);
            if(temp.ip.equals(IPinfor.ip) && temp.port == IPinfor.port && temp.launch_time_id.equals(IPinfor.launch_time_id))
                return true;
            if(temp.ip.equals(IPinfor.ip) && temp.port == IPinfor.port && !(temp.launch_time_id.equals(IPinfor.launch_time_id))) {
                list.remove(i);
                return false;
            }
        }
        return false;
    }

    public static void changePCNickName(String name, String mac) {
        for(int i = 0; i < PCList_Y.size(); ++i)
            if(PCList_Y.get(i).mac.equals(mac))
                PCList_Y.get(i).nickName = name;
    }

    public static void changeTVNickName(String name, String mac) {
        for(int i = 0; i < TVList.size(); ++i)
            if(TVList.get(i).mac.equals(mac))
                TVList.get(i).nickName = name;
    }

    public static void addPCInfor(IPInforBean pcIpInfor) {
        if(!isIPInforContained(pcIpInfor, PCList_Y)) {
            PCList_Y.add(pcIpInfor);
            isNewPC_YList = true;
        }
    }

    public static void addTVInfor(IPInforBean tvIpInfor) {
        if(!isIPInforContained(tvIpInfor, TVList)) {
            TVList.add(tvIpInfor);
            isNewTVList = true;
        }
    }


    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String getAdresseMAC(Context context) {
        WifiManager wifiMan = (WifiManager)context.getSystemService(Context.WIFI_SERVICE) ;
        WifiInfo wifiInf = wifiMan.getConnectionInfo();

        if(wifiInf !=null && marshmallowMacAddress.equals(wifiInf.getMacAddress())){
            String result = null;
            try {
                result= getAdressMacByInterface();
                if (result != null){
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else{
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    private static String getAdressMacByInterface(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:",b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    private static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }
}