package com.dkzy.areaparty.phone.myapplication;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dkzy.areaparty.phone.AESc;
import com.dkzy.areaparty.phone.CrashHandler;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.Login;
import com.dkzy.areaparty.phone.MyConnector;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.R;
import com.dkzy.areaparty.phone.androideventbusutils.events.SelectedDeviceChangedEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.dkzy.areaparty.phone.androideventbusutils.events.changeSelectedDeviceNameEvent;
import com.dkzy.areaparty.phone.fragment01.model.SharedfileBean;
import com.dkzy.areaparty.phone.fragment01.utils.HttpServer;
import com.dkzy.areaparty.phone.fragment01.websitemanager.readSms.ReadSmsService;
import com.dkzy.areaparty.phone.fragment01.websitemanager.start.StartActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.vipShare.VipRentActivity;
import com.dkzy.areaparty.phone.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.dkzy.areaparty.phone.fragment03.utils.TVAppHelper;
import com.dkzy.areaparty.phone.fragment06.DownloadFolderFragment;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView;
import com.dkzy.areaparty.phone.myapplication.floatview.FloatView2;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;
import com.dkzy.areaparty.phone.myapplication.inforUtils.Update_ReceiveMsgBean;
import com.dkzy.areaparty.phone.myapplication.inforUtils.Update_SendMsgBean;
import com.dkzy.areaparty.phone.newAES;
import com.dkzy.areaparty.phone.utils_comman.CommandUtil;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.RequestFormat;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetBroadcastReceiver;
import com.dkzy.areaparty.phone.utils_comman.netWork.NetUtil;
import com.dkzy.areaparty.phone.utilseverywhere.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okserver.OkDownload;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.simple.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import protocol.Msg.LogoutMsg;
import protocol.ProtoHead;
import server.NetworkPacket;

import static android.R.attr.tag;
import static com.dkzy.areaparty.phone.IPAddressConst.statisticServer_ip;
import static com.dkzy.areaparty.phone.fragment01.websitemanager.readSms.ReadSmsService.ONCLICK;
import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;


/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/2 15:28
 */

public class MyApplication extends Application implements NetBroadcastReceiver.netEventHandler {
    public static String AREAPARTY_NET;//服務器ip地址
    public static String domain = "www.areaparty.net";//用户服務器域名
    public static String domain1 = "www.areaparty.com";//统计服務器域名
    public static String location = "";
//119.23.12.116
    public static FloatView mFloatView;
    public static FloatView2 mFloatView2;

    private BroadcastReceiver receiver_onclick = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ONCLICK)) {
                Log.w("ReadSmsService",ONCLICK);
                VipRentActivity.openPackage(getContext(), getContext().getPackageName());
            }
        }
    };

    private static int mNetWorkState;      // 当前网络类型
    public static Context context;
    private static MyApplication instance;
    private static HttpServer dlnaServer;
    private List<Activity> activities = new LinkedList<>();
    private static String launch_time_id;
    private static List<SharedfileBean> mySharedFiles = null;  // 用于存储解封的数据
    public static IPInforBean selectedTVIP;
    public static IPInforBean selectedPCIP;     // 用于存储通信使用TV和PC
    public static HashMap<String, String> TVMacs, PCMacs;         // 用于存储之前连接成功过的TV、PC
    private static Timer stateRefreshTimer, versionCheckTimer;                    // 定时监测选中TV和PC状态的定时器
    private static boolean selectedPCOnline = false;
    private static boolean selectedTVOnline = false;
    public static boolean selectedPCVerified = false;
    public static boolean selectedTVVerified = false;
    public static final String CHECK_ACCESSIBILITY_ISOPEN= "Check_Accessibility" ;
    public static  boolean AccessibilityIsOpen=false;
    private static Update_ReceiveMsgBean receiveMsgBean = new Update_ReceiveMsgBean();
    public static String password = null;
    public static String pass = null;
    public static String pass1 = null;
    public static boolean isSelectedPCOnline() {
        return selectedPCOnline;
    }
    public static void setSelectedPCOnline(boolean b) {
        selectedPCOnline = b;
    }

    public static boolean isSelectedTVOnline() {
        return selectedTVOnline;
    }
    public static void setSelectedTVOnline(boolean b) {
        selectedTVOnline = b;
    }

    public static IPInforBean getSelectedTVIP() {
        return selectedTVIP;
    }

    public static IPInforBean getSelectedPCIP() {
        return selectedPCIP;
    }

    public static void setSelectedPCIP(IPInforBean selectedPCIP) {
        // 发出设备改变的广播
        MyApplication.selectedPCIP = selectedPCIP;
        selectedPCOnline = true;
        MyConnector.getInstance().initial(selectedPCIP.ip);
        EventBus.getDefault().post(new SelectedDeviceChangedEvent(selectedPCOnline, selectedPCIP), "selectedPCChanged");

        getPcAreaPartyPath();


    }

    public static void getWebSiteUrl(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url("http://"+ IPAddressConst.statisticServer_ip+"/bt_website/get_data.json").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                //Log.w("getWebSiteUrl",responseData);
                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    StartActivity.url = new String[5];
                    StartActivity.imageUrl = new String[5];
                    StartActivity.type = new int[5];
                    for (int i =0 ; i< jsonArray.length() ; i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        StartActivity.url[i] = jsonObject.getString("url");
                        StartActivity.imageUrl[i] = "http://"+ IPAddressConst.statisticServer_ip+"/bt_website/"+jsonObject.getString("image");
                        StartActivity.type[i] = jsonObject.getInt("type");
                        //Log.w("getWebSiteUrl",StartActivity.url[i] + StartActivity.imageUrl[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public static void getPcAreaPartyPath(){//发送请求获取AreaParty下载目录
        if (selectedPCOnline && !TextUtils.isEmpty(selectedPCIP.ip)){
            try {
                RequestFormat request = new RequestFormat();
                request.setName(OrderConst.GET_AREAPARTY_PATH);
                request.setCommand("");
                request.setParam("");
                final String requestString = JsonUitl.objectToString(request);
                Log.w("GET_AREAPARTY_PATH",requestString);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String msgReceived = MyConnector.getInstance().getActionStateMsg(requestString);
                        if (TextUtils.isEmpty(msgReceived)) {
                            Log.w("GET_AREAPARTY_PATH", "msgReceived is null");
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(msgReceived);
                            Log.w("GET_AREAPARTY_PATH",msgReceived);
                            String path = jsonObject.getString("message");
                            Log.w("GET_AREAPARTY_PATH",path);
                            if (!TextUtils.isEmpty(path)){
                                RemoteDownloadActivity.rootPath = path;
                                RemoteDownloadActivity.btFilesPath = path + "\\BTdownload\\Torrent\\";
                                RemoteDownloadActivity.targetPath = path + "\\BTdownload\\forLoad\\";
                                RemoteDownloadActivity.downloadPath = path + "\\BTdownload\\download\\";
                                DownloadFolderFragment.rootPath = path + "\\FriendsDownload\\";
                                Log.w("GET_AREAPARTY_PATH", RemoteDownloadActivity.btFilesPath);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }catch (Exception e){e.printStackTrace(); Log.w("GET_AREAPARTY_PATH","Exception");}

            /*if (TextUtils.isEmpty(UrlUtils.token)){
                RemoteDownloadActivity.initUTorrent();
            }*/
        }
    }

    public static void setSelectedTVIP(IPInforBean selectedTVIP) {
        // 发出设备改变的广播
        MyApplication.selectedTVIP = selectedTVIP;
        selectedTVOnline = true;
        EventBus.getDefault().post(new SelectedDeviceChangedEvent(selectedTVOnline, selectedTVIP), "selectedTVChanged");

    }

    public static void changeSelectedTVName(String newName) {
        if(newName != null && !newName.equals(selectedTVIP.nickName)) {
            MyApplication.selectedTVIP.nickName = newName;
            FillingIPInforList.changeTVNickName(newName, selectedTVIP.mac);
            String tvJsonString = JsonUitl.objectToString(selectedTVIP);
            new PreferenceUtil(context).write("lastChosenTV", tvJsonString);
            new PreferenceUtil(context).write(selectedTVIP.mac, newName);
            EventBus.getDefault().post(new changeSelectedDeviceNameEvent(newName), "selectedTVNameChange");
            if(MyApplication.getSelectedPCIP()!=null&& MyApplication.getSelectedTVIP()!=null){
                TVAppHelper.currentPcInfo2TV();
            }
        }
    }

    public static void changeSelectedPCName(String newName) {
        if(newName != null && !newName.equals(selectedPCIP.name)) {
            MyApplication.selectedPCIP.nickName = newName;
            FillingIPInforList.changePCNickName(newName, selectedPCIP.mac);
            String pcJsonString = JsonUitl.objectToString(selectedPCIP);
            new PreferenceUtil(context).write("lastChosenPC", pcJsonString);
            new PreferenceUtil(context).write(selectedPCIP.mac, newName);
            EventBus.getDefault().post(new changeSelectedDeviceNameEvent(newName), "selectedPCNameChange");
            if(MyApplication.getSelectedPCIP()!=null&& MyApplication.getSelectedTVIP()!=null){
                TVAppHelper.currentPcInfo2TV();
            }
        }
    }

    public static void verifyLastPCMac() {
        if(selectedPCIP != null && !selectedPCIP.ip.equals("") && PCMacs != null) {
            final String IP = selectedPCIP.ip;
            final int port = selectedPCIP.port;
            final String code1 = PCMacs.get(selectedPCIP.mac);
            Log.i("sssssssss", "盛大的发生大多萨 " + code1);
            final String code=stringToMD5(code1);
            if(code != "") {
                new Thread(){
                    @Override
                    public void run() {
                        String cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyPCCommand(code));
                        String dataRe = "";
                        Socket client = new Socket();
                        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                        try {
                            String cmdStr1 = AESc.EncryptAsDoNet(cmdStr,code.substring(0,8));
                            client.connect(new InetSocketAddress(IP, port), 1000);
                            IOUtils.write(cmdStr1, client.getOutputStream(), "UTF-8");

//                            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                            dataRe = reader.readLine();
                            IOUtils.copy(client.getInputStream(), outBytes, 8192);
                            dataRe = new String(outBytes.toByteArray(), "UTF-8");

                            password = new PreferenceUtil(context).read("PCMACS");
                            HashMap<String, String>  PCMacs = MyApplication.parse(password);
                            pass=PCMacs.get(selectedPCIP.mac);
                            pass1=stringToMD5(pass);
                            Log.e("22222222", "sdadasdasda" + pass1);
                            String decryptdata = AESc.DecryptDoNet(dataRe,pass1.substring(0,8));
                            Log.e("22222222", "bdafvbfcvcg" + decryptdata);
                            if(decryptdata.length() > 0) {
                                ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(decryptdata, ReceivedActionMessageFormat.class);
                                if(receivedMsg.getStatus() == OrderConst.success) {
                                    if(receivedMsg.getData().equals("true"))
                                        selectedPCVerified = true;
                                    else selectedPCVerified = false;
                                }
                            } else {
                                selectedPCVerified = false;
                            }
                        }catch (IOException e) {
                            selectedPCVerified = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }


    //  还要按蒙蒙返回格式修改
    public static void verifyLastTVMac() {
        if(selectedTVIP != null && !selectedTVIP.ip.equals("") && TVMacs!=null) {
            final String IP = selectedTVIP.ip;
            final int port = selectedTVIP.port;
            final String code1 = TVMacs.get(selectedTVIP.mac);
            final String code=stringToMD5(code1);;
            if(code != "") {
                new Thread(){
                    @Override
                    public void run() {
                        String cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyTVCommand(code)) + "\n";
                        Log.e("22222222", "wowowoowow " + code);
                        String dataRe = "";
                        Socket client = new Socket();
                        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                        try {
                            client.connect(new InetSocketAddress(IP, port), 1000);
                            IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
                            IOUtils.copy(client.getInputStream(), outBytes, 8192);
                            dataRe = new String(outBytes.toByteArray(), "UTF-8");

                            Log.e("22222222", "wowowoowow111 " + dataRe);
                            if(dataRe.equals("false")) {
                                selectedTVVerified = false;
                            } else {
                                selectedTVVerified = true;
                            }
                        }catch (IOException e) {
                            selectedTVVerified = false;
                        }
                    }
                }.start();
            }
        }
    }

    /**
     * <summary>
     *  开启定时器,定时检测选中的TV、PC是否可用
     * </summary>
     */
    private static void startStateRefreshTimer() {
        stateRefreshTimer = new Timer();
        stateRefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean PCOnline = false;
                boolean TVOnline = false;
                if(selectedPCIP != null && !selectedPCIP.ip.equals("")) {
                    Socket client = new Socket();
                    try {
                        client.connect(new InetSocketAddress(selectedPCIP.ip, selectedPCIP.port), 2000);

                        Log.e("stateChange", "连接PC成功");
                        PCOnline = true;
                        FillingIPInforList.addPCInfor(selectedPCIP);
                        if (TextUtils.isEmpty(RemoteDownloadActivity.rootPath)){getPcAreaPartyPath();}
                        if (!selectedPCVerified){verifyLastPCMac();}
                    } catch (IOException e) {
                        Log.e("stateChange", "连接PC失败" + selectedPCIP.ip);
                        PCOnline = false;
                    } finally {
                        if(!client.isClosed())
                            IOUtils.closeQuietly(client);
                    }
                }

                if(selectedTVIP != null && !selectedTVIP.ip.equals("")) {
                    Socket client = new Socket();
                    try {
                        client.connect(new InetSocketAddress(selectedTVIP.getIp(), IPAddressConst.TVRECEIVEPORT_MM), 2000);

                        //TODO:10.18.2017 告知app当前无障碍服务情况
                        String cmd= JSON.toJSONString(CommandUtil.createCheckTvAccessibilityCommand());

                        password = new PreferenceUtil(context).read("TVMACS");
                        HashMap<String, String> TVMacs = MyApplication.parse(password);
                        pass = TVMacs.get(selectedTVIP.mac);
                        pass1 = stringToMD5(pass);
                        if (TextUtils.isEmpty(pass1)) return;
                        Log.e("22222222", "两个rdp" + cmd);
                        String cmdStr1 = newAES.encrypt(cmd, pass1.getBytes());
                        Log.e("22222222", "两个rdpsss" + pass1);

                        PrintStream out = new PrintStream(client.getOutputStream());
                        out.println(cmdStr1);


                        out.flush();


                        BufferedReader buffer = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                        String data = buffer.readLine();
                        String decryptdata= newAES.decrypt(data,pass1.getBytes());
                        Log.e("22222222", "两个rdp111" + decryptdata);
                        Log.e(tag + "ervincm", "Get data from [" + client.getRemoteSocketAddress().toString() + "] : " + data);
                        if (!TextUtils.isEmpty(decryptdata)) {
                            if (decryptdata.equals("true")) {
                                MyApplication.AccessibilityIsOpen = true;
                            } else {
                                MyApplication.AccessibilityIsOpen = false;
                            }
                        }


                        out.close();

                        TVOnline = true;
                        FillingIPInforList.addTVInfor(selectedTVIP);
                        Log.e("stateChange", "连接TV成功");
                        if(selectedPCIP!=null){
                            TVAppHelper.currentPcInfo2TV();
                        }
                        if (!selectedTVVerified){verifyLastTVMac();}
                    } catch (IOException e) {
                        TVOnline = false;
                        Log.e("stateChange", "连接TV失败");
                    } finally {
                        if(!client.isClosed())
                            IOUtils.closeQuietly(client);
                    }
                }

//                if(MyApplication.getSelectedPCIP()!=null&&MyApplication.getSelectedTVIP()!=null&&TVOnline&&PCOnline){
//                    TVAppHelper.currentPcInfo2TV();
//                }

                selectedPCOnline = PCOnline;
                selectedTVOnline = TVOnline;
                Log.e("stateChange", "发出消息");
                EventBus.getDefault().post(new TVPCNetStateChangeEvent(TVOnline, PCOnline), "selectedDeviceStateChanged");
            }
        }, 0, 5000);
    }

    /**
     * <summary>
     *  开启定时器,定时检测是否有新版本
     * </summary>
     */
    private static void startCheckIsNewVersionExist() {
        final String version = MyApplication.getInstance().getVersion();
        versionCheckTimer = new Timer();
        versionCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                String sendStr = JsonUitl.objectToString(new Update_SendMsgBean("app", version));
                try {
                    InetAddress address = InetAddress.getByName(statisticServer_ip);
                    DatagramPacket datagramPacket = new DatagramPacket(sendStr.getBytes(), sendStr.length(),
                            address, IPAddressConst.checkNewVersion_port);
                    DatagramSocket socket = new DatagramSocket();
                    socket.send(datagramPacket);
                    Log.e("MyApplication", "检查更新的消息" + sendStr);
                    byte[] backBuf = new byte[1024];
                    DatagramPacket backPacket = new DatagramPacket(backBuf, backBuf.length);
                    socket.receive(backPacket);
                    String backMsg = new String(backBuf, 0, backPacket.getLength());
                    Log.e("MyApplication", "收到更新的消息" + backMsg);
                    receiveMsgBean = JsonUitl.stringToBean(backMsg, Update_ReceiveMsgBean.class);
                    EventBus.getDefault().post(receiveMsgBean, "newVersionInforChecked");
                    socket.close();
                    if(backMsg.length() > 0)
                        versionCheckTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    private String getVersion() {
            PackageManager manager = context.getPackageManager();
            try {
                //第二个参数代表额外的信息，例如获取当前应用中的所有的Activity
                PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                return packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return "";
    }

    /**
     * <summary>
     *  初始化默认选中的PC和TV地址信息
     * </summary>
     */
    private static void initTVPCIP() {
        String PCMacsStr = new PreferenceUtil(context).read("PCMACS");//连接时的密码
        String TVMacsStr = new PreferenceUtil(context).read("TVMACS");//连接时的密码
        String TVString = new PreferenceUtil(context).read("lastChosenTV");
        String PCString = new PreferenceUtil(context).read("lastChosenPC");
        Log.e("PCMacsStr",PCMacsStr+PCString);
        try {
            PCMacs = parse(PCMacsStr);
            TVMacs = parse(TVMacsStr);
        }catch (Exception e){
            TVMacs = new HashMap<>();
            PCMacs = new HashMap<>();
        }

        if (TVString != null && !TVString.equals("")) {
            selectedTVIP = JsonUitl.stringToBean(TVString, IPInforBean.class);

        }
        if(PCString != null && !PCString.equals("")) {
            selectedPCIP = JsonUitl.stringToBean(PCString, IPInforBean.class);
            MyConnector.getInstance().initial(selectedPCIP.ip);
            getPcAreaPartyPath();
        }


        if(MyApplication.getSelectedPCIP()!=null&& MyApplication.getSelectedTVIP()!=null){
            TVAppHelper.currentPcInfo2TV();
        }




    }

    public static HashMap<String, String> parse(String str) {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(str));
        reader.setLenient(true);
        HashMap<String, String> list = gson.fromJson(reader, new TypeToken<HashMap<String, String>>(){}.getType());
        return list;
    }

    public static void addTVMac(String mac, String code) {
        if(TVMacs == null) {
            TVMacs = new HashMap<>();
        }
        TVMacs.put(mac, code);
        String str = JsonUitl.objectToString(TVMacs);
        new PreferenceUtil(context).write("TVMACS", str);
    }

    public static void addPCMac(String mac, String code) {
        if(PCMacs == null) {
            PCMacs = new HashMap<>();
        }
        PCMacs.put(mac, code);
        String str = JsonUitl.objectToString(PCMacs);
        new PreferenceUtil(context).write("PCMACS", str);
    }

    public static void removePCMac(String mac) {
        if(PCMacs != null) {
            PCMacs.remove(mac);
        }
        String str = JsonUitl.objectToString(PCMacs);
        new PreferenceUtil(context).write("PCMACS", str);
    }

    public static void removeTVMac(String mac) {
        if(TVMacs != null) {
            TVMacs.remove(mac);
        }
        String str = JsonUitl.objectToString(TVMacs);
        new PreferenceUtil(context).write("TVMACS", str);
    }

    public static boolean isTVMacContains(String mac) {
        if (TVMacs!=null){
            return TVMacs.containsKey(mac);
        }
        return false;
    }

    public static boolean isPCMacContains(String mac) {
        if (PCMacs != null){
            return PCMacs.containsKey(mac);
        }
        return false;
    }

    public static List<SharedfileBean> getMySharedFiles() {
        if(mySharedFiles == null)
            mySharedFiles = new LinkedList<>();
        return mySharedFiles;
    }

    public static void addMySharedFiles(SharedfileBean mySharedFiles) {
        if(MyApplication.mySharedFiles == null)
            MyApplication.mySharedFiles = new ArrayList<>();
        MyApplication.mySharedFiles.add(mySharedFiles);
    }

    @Override
    public void onNetChange() {
        Log.e("MyApplication", "netchange");
        if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_NONE) {
            OkDownload.getInstance().pauseAll();
            FillingIPInforList.setCloseSignal(true);
            Log.e("MyApplication", "网络不可用");
        } else if(NetUtil.getNetWorkState(this) == NetUtil.NETWORK_WIFI) {
            Log.e("MyApplication", "WIFI已连接");
            FillingIPInforList.setCloseSignal(false);
            FillingIPInforList.startBroadCastAndListen(10000, 10000);
            getAddress();
            ReadSmsService.reLogin();
            Log.w("login","wifi重新登录");
        } else {
            OkDownload.getInstance().pauseAll();
            FillingIPInforList.setCloseSignal(true);
            Log.e("MyApplication", "当前是移动网");
            getAddress();
            ReadSmsService.reLogin();
            Log.w("login","移动网重新登录");
        }


    }
    /*根据网络接口查询所在城市*/
    public static void getAddress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("https://ip.cn/").build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Document doc = Jsoup.parse(responseData);
                    Element element = doc.getElementsByClass("well").first();
                    Element element1 = element.child(element.childNodeSize()-1);
                    String s = element1.text();
                    location = s.substring(s.indexOf(" ")+1,s.indexOf(","));
                    Log.w("地理位置",location);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AREAPARTY_NET = GetInetAddress(domain);
                statisticServer_ip = GetInetAddress(domain1);
                //statisticServer_ip = "192.168.1.139";
                //statisticServer_ip = "119.23.74.220";
            }
        }).start();

        Utils.init(this);
        if (mFloatView == null){
            mFloatView = new FloatView(getApplicationContext());
        }
        if (mFloatView2 == null){
            mFloatView2 = new FloatView2(getApplicationContext());
        }

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this,config);
        context = getApplicationContext();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        okGoInit();
        initTVPCIP();

        mNetWorkState = NetUtil.getNetWorkState(this);
        NetBroadcastReceiver receiver = new NetBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
        NetBroadcastReceiver.mListeners.add(this);

        launch_time_id = String.valueOf(new Random(System.currentTimeMillis()).nextInt());
        // 打开本地Http服务器
        try {
            dlnaServer = new HttpServer(IPAddressConst.DLNAPHONEHTTPPORT_B);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            verifyLastPCMac();
            verifyLastTVMac();
        }catch (Exception e){e.printStackTrace();}

        getWebSiteUrl();

        startStateRefreshTimer();
        startCheckIsNewVersionExist();

        registerNotificationReceiver();
    }
    private void registerNotificationReceiver(){
        IntentFilter filter_click = new IntentFilter();
        filter_click.addAction(ONCLICK);
        //注册广播
        registerReceiver(receiver_onclick, filter_click);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stateRefreshTimer.cancel();
    }

    /**
     * <summary>
     *  获取网络状态变量
     * </summary>
     * <returns>网络状态</returns>
     */
    public static int getmNetWorkState() {
        return mNetWorkState;
    }

    /**
     * <summary>
     *  设置网络状态
     * </summary>
     * <param name="mNetWorkState">网络状态</param>
     */
    public static void setmNetWorkState(int mNetWorkState) {
        MyApplication.mNetWorkState = mNetWorkState;
    }

    /**
     * <summary>
     *  初始化下载器
     * </summary>
     */
    private void okGoInit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);

        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //设置OkHttpClient
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }

    /**
     * <summary>
     *  获取application的单例
     * </summary>
     * <returns>MyApplication</returns>
     */
    public synchronized static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }
    /**
     * <summary>
     *  获取上下文context
     * </summary>
     * <returns>context</returns>
     */
    public static Context getContext() {
        return context;
    }

    /**
     * <summary>
     * 获取应用启动标识
     * </summary>
     * <returns>launchTimeId</returns>
     */
    public static String getLaunchTimeId() {
        return launch_time_id;
    }

    /**
     * <summary>
     *  添加activity到Application的list中
     * </summary>
     * <param name="activity">当前创建的activity</param>
     */
    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void goToLogin(){
        sendLogoutMessage();
        boolean b= false;
        for (Activity activity : activities) {
            if (activity instanceof Login){
                b = true;
                continue;
            }
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
        if (!b){
            startActivity(new Intent(this, Login.class));
        }
    }

    public void sendLogoutMessage(){
        if (!TextUtils.isEmpty(Login.userId)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        LogoutMsg.LogoutReq.Builder builder = LogoutMsg.LogoutReq.newBuilder();
                        builder.setLogoutType(LogoutMsg.LogoutReq.LogoutType.MOBILE);
                        builder.setUserId(Login.userId);
                        byte[] reByteArray = NetworkPacket.packMessage(ProtoHead.ENetworkMessage.LOGOUT_REQ.getNumber(), builder.build().toByteArray());
                        if (Login.base != null){
                            Login.base.writeToServer(Login.outputStream, reByteArray);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    /**
     * <summary>
     *  完整退出应用
     * </summary>
     */
    public void exit() {
        try {
            for (Activity activity : activities) {
                try {
                    if (!activity.isFinishing()){
                        activity.finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * <summary>
     *  关闭广播
     * </summary>
     */
    public void closeBroadCast() {
        FillingIPInforList.setCloseSignal(true);
    }

    /**
     * <summary>
     *  获取当前接收到的TV地址列表
     * </summary>
     */
    public static ArrayList<IPInforBean> getTVIPInforList() {
        return FillingIPInforList.getTVList();
    }

    /**
     * <summary>
     *  获取当前接收到的PC地址列表
     * </summary>
     */
    public static ArrayList<IPInforBean> getPC_YInforList() {
        return FillingIPInforList.getPCList_Y();
    }

    /**
     * <summary>
     * 获取手机在wifi下的IP
     * </summary>
     * <returns>String</returns>
     */
    public static String getIPStr() {
        return FillingIPInforList.getIpStr();
    }

    /**
     * <summary>
     * 获取手机当前连接wifi名称
     * </summary>
     * <returns>String</returns>
     */
    public static String getWifiName() {
        return FillingIPInforList.getWifiName();
    }

    /**
     * <summary>
     * 获取包含新版本信息的bean
     * </summary>
     * <returns>Update_ReceiveMsgBean</returns>
     */
    public static Update_ReceiveMsgBean getReceiveMsgBean() {
        return receiveMsgBean;
    }

    public void closeAll(){
        for(Activity activity : activities){
            try {
                if (!activity.isFinishing()){
                    activity.finish();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static String GetInetAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return IPAddress;
        }
        return IPAddress;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public  void showNotification(final String title, final String contentText){
        Looper.prepare();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Intent Intent_pre = new Intent(ONCLICK);
                PendingIntent pendIntent_click = PendingIntent.getBroadcast(context, 0, Intent_pre, 0);

                Notification notification = new Notification.Builder(context).setContentTitle(title)
                        .setContentText(contentText).setStyle(new Notification.BigTextStyle().bigText(contentText)).setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.app_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.app_logo))
                        .setContentIntent(pendIntent_click).setAutoCancel(true).build();

                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                manager.notify(1,notification);
            }
        });
        Looper.loop();
    }

    public static void showToast(final String text){
        Looper.prepare();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });

        Looper.loop();
    }
}
