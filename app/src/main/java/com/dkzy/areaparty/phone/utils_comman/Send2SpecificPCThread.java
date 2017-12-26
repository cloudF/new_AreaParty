package com.dkzy.areaparty.phone.utils_comman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dkzy.areaparty.phone.AESc;
import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.ReceivedActionMessageFormat;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;


/**
 * Created by borispaul on 2017/6/23.
 */

public class Send2SpecificPCThread extends Thread {
    private static final int SOCKET_TIMEOUT = 5000;
    private String typeName;
    private String commandType = "";
    private Map<String, String> param = new HashMap<>();
    private String IP = "";
    private int port;
    private String code;

    private Handler myhandler;

    /**
     * <summary>
     *  构造函数
     * </summary>
     * <param name="typeName">类别名称</param>
     * <param name="commandType">操作类别</param>
     * <param name="myhandler">消息传递句柄</param>
     */
    public Send2SpecificPCThread(String typeName, String commandType, Handler myhandler, String IP, int port, String code) {
        this.typeName = typeName;
        this.commandType = commandType;
        this.myhandler = myhandler;
        this.IP = IP;
        this.port = port;
        String pass=stringToMD5(code);
        this.code = pass;
    }

    @Override
    public void run() {
        if(!IP.equals("")) {
            String cmdStr = createCmdStr();
            String dataReceived = "";
            Socket client = new Socket();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            try {
                String cmdStr1 = AESc.EncryptAsDoNet(cmdStr,code.substring(0,8));
                System.out.println("md5后的配对码" +cmdStr1);
                client.connect(new InetSocketAddress(IP, port), SOCKET_TIMEOUT);


                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                writer.write(cmdStr1);
                writer.newLine();
                writer.flush();

                dataReceived = reader.readLine();
                String  decryptdata = AESc.DecryptDoNet(dataReceived,code.substring(0,8));

                Log.i("Send2PCThread", "指令: " + cmdStr);
                Log.i("Send2PCThread", "回复: " + decryptdata);
                if(decryptdata.length() > 0) {
                    try {
                        ReceivedActionMessageFormat receivedMsg = JsonUitl.stringToBean(decryptdata, ReceivedActionMessageFormat.class);
                        if(receivedMsg.getStatus() == OrderConst.success) {
                            if(receivedMsg.getData() != null)
                                parseMesgReceived(receivedMsg.getData());
                            reportResult(true, receivedMsg.getData());
                        } else {
                            reportResult(false, null);
                        }
                    } catch (Exception e) {
                        Log.i("Send2PCThread", "catch" + e.getMessage());
                        reportResult(false, null);
                    }
                } else reportResult(false, null);
            }catch (IOException e) {
                e.printStackTrace();
                reportResult(false, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!client.isClosed()) {
                    IOUtils.closeQuietly(client);
                }
            }
        } else reportResult(false, null);

    }

    /**
     * <summary>
     *  创建请求指令字符串
     * </summary>
     * <returns>发送给PC的请求指令</returns>
     */
    private String createCmdStr() {
        String cmdStr = "";
        switch (typeName) {
            case OrderConst.identityAction_name:
                cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyPCCommand(code));
                break;
        }
        return cmdStr;
    }

    /**
     * <summary>
     *  解析PC返回的数据的data部分并设置相应的静态变量
     * </summary>
     */
    private void parseMesgReceived(String data) {
        switch (typeName) {
            case OrderConst.identityAction_name:
                break;
        }
    }

    /**
     * <summary>
     *  发送相应的Handler消息
     * </summary>
     */
    private void reportResult(boolean result, String data) {
        if (result) {
            switch (typeName) {
                case OrderConst.identityAction_name: {
                    Message message = new Message();
                    message.what = 200;
                    message.obj = data;
                    myhandler.sendMessage(message);
                }
                    break;
            }
        } else {
            switch (typeName) {
                case OrderConst.identityAction_name: {
                    Message message = new Message();
                    message.what = 404;
                    message.obj = data;
                    myhandler.sendMessage(message);
                }
                break;
            }
        }
    }
}
