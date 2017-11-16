package com.androidlearning.boris.familycentralcontroler.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.androidlearning.boris.familycentralcontroler.IPAddressConst;
import com.androidlearning.boris.familycentralcontroler.MyConnector;
import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.androideventbusutils.events.SelectedDeviceChangedEvent;
import com.androidlearning.boris.familycentralcontroler.androideventbusutils.events.TVPCNetStateChangeEvent;
import com.androidlearning.boris.familycentralcontroler.androideventbusutils.events.changeSelectedDeviceNameEvent;
import com.androidlearning.boris.familycentralcontroler.fragment01.model.SharedfileBean;
import com.androidlearning.boris.familycentralcontroler.fragment01.utils.HttpServer;
import com.androidlearning.boris.familycentralcontroler.fragment01.websitemanager.web1080.RemoteDownloadActivity;
import com.androidlearning.boris.familycentralcontroler.fragment01.utils.prepareDataForFragment;
import com.androidlearning.boris.familycentralcontroler.fragment03.utils.TVAppHelper;
import com.androidlearning.boris.familycentralcontroler.myapplication.inforUtils.FillingIPInforList;
import com.androidlearning.boris.familycentralcontroler.myapplication.inforUtils.Update_ReceiveMsgBean;
import com.androidlearning.boris.familycentralcontroler.myapplication.inforUtils.Update_SendMsgBean;
import com.androidlearning.boris.familycentralcontroler.utils_comman.CommandUtil;
import com.androidlearning.boris.familycentralcontroler.utils_comman.PreferenceUtil;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.IPInforBean;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.JsonUitl;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.ReceivedActionMessageFormat;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.ReceivedDiskListFormat;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.ReceivedExeListFormat;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.RequestFormat;
import com.androidlearning.boris.familycentralcontroler.utils_comman.netWork.NetBroadcastReceiver;
import com.androidlearning.boris.familycentralcontroler.utils_comman.netWork.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okserver.OkDownload;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

import static android.R.attr.tag;


/**
 * Project Name： FamilyCentralControler
 * Description:
 * Author: boris
 * Time: 2017/3/2 15:28
 */

public class MyApplication extends Application implements NetBroadcastReceiver.netEventHandler {
    private static int mNetWorkState;      // 当前网络类型
    private static Context context;
    private static MyApplication instance;
    private static HttpServer dlnaServer;
    private List<Activity> activities = new LinkedList<>();
    private static String launch_time_id;
    private static List<SharedfileBean> mySharedFiles = null;  // 用于存储解封的数据
    private static IPInforBean selectedTVIP, selectedPCIP;     // 用于存储通信使用TV和PC
    public static HashMap<String, String> TVMacs, PCMacs;         // 用于存储之前连接成功过的TV、PC
    private static Timer stateRefreshTimer, versionCheckTimer;                    // 定时监测选中TV和PC状态的定时器
    private static boolean selectedPCOnline = false;
    private static boolean selectedTVOnline = false;
    public static boolean selectedPCVerified = false;
    public static boolean selectedTVVerified = false;
    public static final String CHECK_ACCESSIBILITY_ISOPEN= "Check_Accessibility" ;
    public static  boolean AccessibilityIsOpen=false;
    private static Update_ReceiveMsgBean receiveMsgBean = new Update_ReceiveMsgBean();

    public static boolean isSelectedPCOnline() {
        return selectedPCOnline;
    }

    public static boolean isSelectedTVOnline() {
        return selectedTVOnline;
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
    public static void getPcAreaPartyPath(){//发送请求获取AreaParty安装目录
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
                            org.json.JSONObject jsonObject = new org.json.JSONObject(msgReceived);
                            Log.w("GET_AREAPARTY_PATH",msgReceived);
                            String path = jsonObject.getString("message");
                            Log.w("GET_AREAPARTY_PATH",path);
                            if (!TextUtils.isEmpty(path)){
                                RemoteDownloadActivity.btFilesPath = path + "BtFiles\\";
                                Log.w("GET_AREAPARTY_PATH",RemoteDownloadActivity.btFilesPath);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }catch (Exception e){e.printStackTrace(); Log.w("GET_AREAPARTY_PATH","Exception");}

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
            if(MyApplication.getSelectedPCIP()!=null&&MyApplication.getSelectedTVIP()!=null){
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
            if(MyApplication.getSelectedPCIP()!=null&&MyApplication.getSelectedTVIP()!=null){
                TVAppHelper.currentPcInfo2TV();
            }
        }
    }

    public static void verifyLastPCMac() {
        if(selectedPCIP != null && !selectedPCIP.ip.equals("") && PCMacs != null) {
            final String IP = selectedPCIP.ip;
            final int port = selectedPCIP.port;
            final String code = PCMacs.get(selectedPCIP.mac);
            if(code != "") {
                new Thread(){
                    @Override
                    public void run() {
                        String cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyPCCommand(code));
                        String dataRe = "";
                        Socket client = new Socket();
                        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                        try {
                            client.connect(new InetSocketAddress(IP, port), 1000);
                            IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
                            IOUtils.copy(client.getInputStream(), outBytes, 8192);
                            dataRe = new String(outBytes.toByteArray(), "UTF-8");
                            if(dataRe.length() > 0) {
                                ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(dataRe, ReceivedActionMessageFormat.class);
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
            final String code = TVMacs.get(selectedTVIP.mac);
            if(code != "") {
                new Thread(){
                    @Override
                    public void run() {
                        String cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyTVCommand(code)) + "\n";
                        String dataRe = "";
                        Socket client = new Socket();
                        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
                        try {
                            client.connect(new InetSocketAddress(IP, port), 1000);
                            IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
                            IOUtils.copy(client.getInputStream(), outBytes, 8192);
                            dataRe = new String(outBytes.toByteArray(), "UTF-8");
                            if(dataRe.equals("true")) {
                                selectedTVVerified = true;
                            } else {
                                selectedTVVerified = false;
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
                        if (TextUtils.isEmpty(RemoteDownloadActivity.btFilesPath)){getPcAreaPartyPath();}
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

                        PrintStream out=new PrintStream(client.getOutputStream());
                        out.println(cmd);


                        out.flush();


                        BufferedReader buffer = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
                        String data = buffer.readLine();
                        Log.e(tag+"ervincm","Get data from [" + client.getRemoteSocketAddress().toString() + "] : " + data);
                        if(!TextUtils.isEmpty(data)){
                            if(data.equals("true")){
                                MyApplication.AccessibilityIsOpen=true;
                            }else {
                                MyApplication.AccessibilityIsOpen=false;
                            }
                        }


                        out.close();

                        TVOnline = true;
                        FillingIPInforList.addTVInfor(selectedTVIP);
                        Log.e("stateChange", "连接TV成功");
                        if(selectedPCIP!=null){
                            TVAppHelper.currentPcInfo2TV();
                        }

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
                    InetAddress address = InetAddress.getByName(IPAddressConst.statisticServer_ip);
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


        if(MyApplication.getSelectedPCIP()!=null&&MyApplication.getSelectedTVIP()!=null){
            TVAppHelper.currentPcInfo2TV();
        }


    }

    private static HashMap<String, String> parse(String str) {
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
        if(PCMacs != null) {
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
        } else {
            OkDownload.getInstance().pauseAll();
            FillingIPInforList.setCloseSignal(true);
            Log.e("MyApplication", "当前是移动网");
        }
    }

    @Override
    public void onCreate() {
        Log.e("MyApplication", "applicationCreate");
        super.onCreate();
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

        startStateRefreshTimer();
        startCheckIsNewVersionExist();
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

    /**
     * <summary>
     *  完整退出应用
     * </summary>
     */
    public void exit() {
        try {
            for (Activity activity : activities) {
                if (activity != null)
                    activity.finish();
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
            activity.finish();
        }
    }
}
