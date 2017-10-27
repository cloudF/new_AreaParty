package com.androidlearning.boris.familycentralcontroler.utils_comman;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.androidlearning.boris.familycentralcontroler.OrderConst;
import com.androidlearning.boris.familycentralcontroler.utils_comman.CommandUtil;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.JsonUitl;
import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.ReceivedActionMessageFormat;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by borispaul on 2017/6/23.
 */

public class Send2SpecificTVThread extends Thread {
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
    public Send2SpecificTVThread(String typeName, String commandType, Handler myhandler, String IP, int port, String code) {
        this.typeName = typeName;
        this.commandType = commandType;
        this.myhandler = myhandler;
        this.IP = IP;
        this.port = port;
        this.code = code;
    }

    @Override
    public void run() {
        if(!IP.equals("")) {
            String cmdStr = createCmdStr();
            String dataReceived = "";
            Socket client = new Socket();
            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            try {
                client.connect(new InetSocketAddress(IP, port), SOCKET_TIMEOUT);
                IOUtils.write(cmdStr, client.getOutputStream(), "UTF-8");
                IOUtils.copy(client.getInputStream(), outBytes, 8192);
                dataReceived = new String(outBytes.toByteArray(), "UTF-8");
                Log.i("Send2TVThread", "指令: " + cmdStr);
                Log.i("Send2TVThread", "回复: " + dataReceived);
                if(dataReceived.equals("true")) {
                   reportResult(true, "true");
                } else reportResult(false, "false");
            }catch (IOException e) {
                e.printStackTrace();
                reportResult(false, "false");
            } finally {
                if (!client.isClosed()) {
                    IOUtils.closeQuietly(client);
                }
            }
        } else reportResult(false, "false");

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
                cmdStr = JsonUitl.objectToString(CommandUtil.createVerifyTVCommand(code)) + "\n";
                break;
        }
        return cmdStr;
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
