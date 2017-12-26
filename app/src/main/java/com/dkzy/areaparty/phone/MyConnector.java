package com.dkzy.areaparty.phone;

import android.util.Log;

import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.LogUtil;
import com.dkzy.areaparty.phone.utils_comman.PreferenceUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.context;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.selectedPCIP;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.selectedTVIP;
import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;

/**
 * Created by boris on 2016/12/16.
 *
 */

public class MyConnector {
    private static MyConnector instance = null;
    private Socket monitoringSocket = null;
    private BufferedWriter monitoringBufferedWriter = null;
    private BufferedReader monitoringBufferedReader = null;

    private String IP = "";
    private boolean connetedState = false;
    private MyConnector() {}
    public static String password = null;
    public static String pass = null;
    public static String pass1 = null;
    public static MyConnector getInstance() {
        if(instance == null) {
            synchronized (MyConnector.class) {
                if(instance == null) {
                    instance = new MyConnector();
                }
            }
        }
        return instance;
    }

    public String getIP() {
        return IP;
    }

    public void initial(String IP) {
        if(!IP.equals(""))
            this.IP = IP;
    }

    /**
     * <summary>
     *  创建监控信息的长连接，并初始化IP和执行操作(短连接)时使用的端口
     * </summary>
     * <param name="IP">IP地址</param>
     * <returns>长连接创建状态</returns>
     */
    public boolean connect() {
        boolean state = false;
        if(IP.equals("") && MyApplication.getSelectedPCIP() != null) {
            String ipPC = MyApplication.getSelectedPCIP().ip;
            initial(ipPC);
        }
        if(MyApplication.getSelectedPCIP() != null && IP != null && !IP.equals("")) {
            if (monitoringSocket == null) {
                try {
                    monitoringSocket = new Socket();
                    monitoringSocket.connect(new InetSocketAddress(IP, IPAddressConst.PCMONITORPORT_B), 2000);
                    monitoringBufferedWriter = new BufferedWriter(new OutputStreamWriter(monitoringSocket.getOutputStream(), "utf-8"));
                    monitoringBufferedReader = new BufferedReader(new InputStreamReader(monitoringSocket.getInputStream(), "utf-8"));
                    Log.e("Myconnector", "长连接成功");
                    state = true;
                } catch (IOException e) {
                    monitoringSocket = null;
                    Log.e("Myconnector", "长连接失败");
                    state = false;
                }
            }
        } else  {
            state = false;
        }
        return state;
    }

    /**
     * <summary>
     *  重新和服务器建立长连接(传输监控数据)
     * </summary>
     * <returns>重建状态</returns>
     */
    private boolean rebuildSocket()
    {
        if(IP.equals("") && MyApplication.getSelectedPCIP() != null) {
            String ipPC = MyApplication.getSelectedPCIP().ip;
            initial(ipPC);
        }

        if(!IP.equals("")) {
            try {
                monitoringSocket = new Socket();
                monitoringSocket.connect(new InetSocketAddress(IP, IPAddressConst.PCMONITORPORT_B), 2000);
                monitoringBufferedWriter = new BufferedWriter(new OutputStreamWriter(monitoringSocket.getOutputStream(), "utf-8"));
                monitoringBufferedReader = new BufferedReader(new InputStreamReader(monitoringSocket.getInputStream(), "utf-8"));
                return true;
            } catch (IOException e) {
                monitoringSocket = null;
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * <summary>
     *  获取当前和服务器的连接状态
     * </summary>
     * <returns>和服务器的连接状态</returns>
     */
    public boolean getConnetedState() {
        return connetedState;
    }

    /**
     * <summary>
     *  使用短连接发送操作指令，并获取执行结果
     * </summary>
     * <param name="msgSend">待发送的命令</param>
     * <returns>执行结果</returns>
     */
    public String getActionStateMsg(String msgSend) {
        if(IP.equals("")) {
            String ipPC = MyApplication.getSelectedPCIP().ip;
            initial(ipPC);
            IP = ipPC;
        }
        String msg = "";
        int len;

        if(!IP.equals("")) {
            try {
                SocketAddress address = new InetSocketAddress(IP, IPAddressConst.PCACTIONPORT_B);
                Socket actionSocket = new Socket();
                actionSocket.connect(address, 2000);
                password = new PreferenceUtil(context).read("PCMACS");
                HashMap<String, String> PCMacs = MyApplication.parse(password);
                pass=PCMacs.get(selectedPCIP.mac);
                pass1=stringToMD5(pass);
                Log.i("myconnetor", "回复: " + msgSend);
                String msgSend1 = AESc.EncryptAsDoNet(msgSend,pass1.substring(0,8));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(actionSocket.getInputStream(), "utf-8"));
                bufferedWriter.write(msgSend1);
                bufferedWriter.flush();

                String dataReceived = bufferedReader.readLine();

                msg = AESc.DecryptDoNet(dataReceived,pass1.substring(0,8));
                Log.i("myconnetor", "解密: " + msg);
                actionSocket.close();
                bufferedReader.close();
                bufferedWriter.close();
                return msg;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else return "";


    }


    /**
     * <summary>
     *  使用短连接发送添加路径到PC的Http服务器指令，并获取执行结果
     * </summary>
     * <param name="msgSend">待发送的命令</param>
     * <returns>执行结果</returns>
     */
    public String getAddPathToHttpStateMsg(String msgSend) {
        if(IP.equals("")) {
            String ipPC = MyApplication.getSelectedPCIP().ip;
            initial(ipPC);
        }

        String msg = "";
        int len;
        if(!IP.equals("")) {
            try {
                SocketAddress address = new InetSocketAddress(IP, MyApplication.getSelectedPCIP().port);//IPAddressConst.PCACTIONPORT_ADDPATHTOHTTP_Y
                Socket actionSocket = new Socket();
                actionSocket.connect(address, 2000);
                password = new PreferenceUtil(context).read("PCMACS");
                HashMap<String, String>  PCMacs = MyApplication.parse(password);
                pass=PCMacs.get(selectedPCIP.mac);
                pass1=stringToMD5(pass);
                String msgSend1 = AESc.EncryptAsDoNet(msgSend,pass1.substring(0,8));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(actionSocket.getInputStream(), "utf-8"));
                bufferedWriter.write(msgSend1);
                bufferedWriter.flush();

//                char[] rev = new char[1024];
//                if((len = bufferedReader.read(rev)) > 0) {
//                    msg = new String(rev, 0, len);
//                }
                String dataReceived = bufferedReader.readLine();
                msg = AESc.DecryptDoNet(dataReceived,pass1.substring(0,8));
                Log.i("myconnetor", "解密111: " + msg);
                actionSocket.close();
                bufferedReader.close();
                bufferedWriter.close();

                return msg;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        } else return "";
    }



    public boolean sendMsgToIP(String ip, int port, String msgSend) {
        boolean signal = false;
        try {
            Socket actionSocket = new Socket(ip, port);
            password = new PreferenceUtil(context).read("TVMACS");
            HashMap<String, String> TVMacs = MyApplication.parse(password);
            pass=TVMacs.get(selectedTVIP.mac);
            pass1=stringToMD5(pass);
            String msgSend1 = newAES.encrypt(msgSend,pass1.getBytes());
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
            bufferedWriter.write(msgSend1);
            bufferedWriter.flush();
            actionSocket.close();
            bufferedWriter.close();
            signal = true;
        } catch (IOException e) {
            e.printStackTrace();
            signal = false;
        }

        return signal;
    }

    /**
     * <summary>
     *  发送指定信息
     * </summary>
     * <param name="msg">消息</param>
     * <returns></returns>
     */
    public boolean sentMonitorCommand(String msg) {
        boolean result;
        if(monitoringBufferedWriter != null) {
            try {
                password = new PreferenceUtil(context).read("PCMACS");
                HashMap<String, String>  PCMacs = MyApplication.parse(password);
                pass=PCMacs.get(selectedPCIP.mac);
                pass1=stringToMD5(pass);
                String msg1 = AESc.EncryptAsDoNet(msg,pass1.substring(0,8));
                monitoringBufferedWriter.write(msg1);
                monitoringBufferedWriter.flush();
                result = true;
                connetedState = true;
            } catch (Exception e) {
                boolean signal = rebuildSocket();
                if(signal) {
                    System.out.println("重连成功");
                    sentMonitorCommand(msg);
                    result = true;
                    connetedState = true;
                } else {
                    result = false;
                    connetedState = false;
                    System.out.println("重连失败");
                }
            }
        }
        else {
            boolean signal = rebuildSocket();
            if(signal) {
                System.out.println("重连成功");
                sentMonitorCommand(msg);
                result = true;
                connetedState = true;
            } else {
                result = false;
                connetedState = false;
                System.out.println("重连失败");
            }
        }
        return result;
    }

    /**
     * <summary>
     *  返回获取到的消息(格式1: 完整的json字符串 格式2: 空字符)
     * </summary>
     * <returns>json串或空字符串</returns>
     */
    public String getMonitorMsg() {
        String msg = "";
        int len;
        if(monitoringBufferedReader != null) {
            char[] rev = new char[8000];
            try {
                while((len = monitoringBufferedReader.read(rev)) > 0) {
                    String temp = new String(rev, 0, len);
                    msg += temp;
                    if(msg.contains("}]}}"))
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        LogUtil.e("MonitorData", "接收到" + msg);
        if((msg.contains("{\"status\"")) && (msg.contains("}}"))) {
            return msg;
        } else {
            return "";
        }
    }

    /**
     * <summary>
     *  关闭长连接
     * </summary>
     */
    public void closeLongConnect() {
        if(monitoringSocket != null) {
            try {
                monitoringSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(monitoringBufferedReader != null) {
            try {
                monitoringBufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(monitoringBufferedWriter != null) {
            try {
                monitoringBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
