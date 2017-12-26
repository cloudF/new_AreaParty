package com.dkzy.areaparty.phone;

import android.util.Log;

import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.utils_comman.LogUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by boris on 2016/12/16.
 *
 */

public class MyConnector_TV {
    private static MyConnector_TV instance = null;
    private Socket monitoringSocket = null;
    private BufferedWriter monitoringBufferedWriter = null;
    private BufferedReader monitoringBufferedReader = null;

    private String IP = "";
    private boolean connetedState = false;
    private MyConnector_TV() {}

    public static MyConnector_TV getInstance() {
        if(instance == null) {
            synchronized (MyConnector_TV.class) {
                if(instance == null) {
                    instance = new MyConnector_TV();
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
        if(IP.equals("") && MyApplication.getSelectedTVIP() != null) {
            String ipTV = MyApplication.getSelectedTVIP().ip;
            initial(ipTV);
        }
        if(MyApplication.getSelectedTVIP() != null && IP != null && !IP.equals("")) {
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
        if(IP.equals("") && MyApplication.getSelectedTVIP() != null) {
            String ipTV = MyApplication.getSelectedTVIP().ip;
            initial(ipTV);
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
        Log.w("IPGET","进入 getActionStateMsg");
        if(IP.equals("")) {
            String ipTV = MyApplication.getSelectedTVIP().ip;
            initial(ipTV);
            IP = ipTV;
        }
        String msg = "";
        int len;

        if(!IP.equals("")) {
            try {

                /*SocketAddress address = new InetSocketAddress(IP, IPAddressConst.PCACTIONPORT_B_TV);
                Socket actionSocket = new Socket();
                actionSocket.connect(address, 2000);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(actionSocket.getInputStream(), "utf-8"));
                bufferedWriter.write(msgSend);
                bufferedWriter.flush();

                char[] rev = new char[4000];
                String temp = null;
                while((temp = bufferedReader.readLine()) != null) {
                    msg += temp;
                }

                actionSocket.close();
                bufferedReader.close();
                bufferedWriter.close();*/
                Socket socket = new Socket(IP, IPAddressConst.PCACTIONPORT_B_TV);

                //String msg = "{\"command\":\"GETDISKLIST\",\"name\":\"DISK\",\"param\":\"\"}";
                PrintWriter pw = new PrintWriter(socket.getOutputStream());
                pw.print(msgSend);
                pw.flush();
                socket.shutdownOutput();
                System.out.println("发送了消息: "+msgSend);

                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = null;
                while((temp=br.readLine())!=null) {
                    System.out.println("接收："+temp);
                    msg = temp;
                }

                br.close();
                isr.close();
                is.close();
                pw.close();
                socket.close();

                return msg;
            } catch (IOException e) {
                e.printStackTrace();
                Log.w("IPGET","socket 异常");
                return "";
            }
        } else {
            Log.w("IPGET","ip 异常");
            return "";
        }


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
            String ipTV = MyApplication.getSelectedTVIP().ip;
            initial(ipTV);
        }

        String msg = "";
        int len;
        if(!IP.equals("")) {
            try {
                SocketAddress address = new InetSocketAddress(IP, MyApplication.getSelectedTVIP().port);//IPAddressConst.PCACTIONPORT_ADDPATHTOHTTP_Y
                Socket actionSocket = new Socket();
                actionSocket.connect(address, 2000);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(actionSocket.getInputStream(), "utf-8"));
                bufferedWriter.write(msgSend);
                bufferedWriter.flush();

                char[] rev = new char[1024];
                if((len = bufferedReader.read(rev)) > 0) {
                    msg = new String(rev, 0, len);
                }
                actionSocket.close();
                bufferedReader.close();
                bufferedWriter.close();

                return msg;
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        } else return "";
    }



    public boolean sendMsgToIP(String ip, int port, String msgSend) {
        boolean signal = false;
        try {
            Socket actionSocket = new Socket(ip, port);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(actionSocket.getOutputStream(), "utf-8"));
            bufferedWriter.write(msgSend);
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
                monitoringBufferedWriter.write(msg);
                monitoringBufferedWriter.flush();
                result = true;
                connetedState = true;
            } catch (IOException e) {
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
