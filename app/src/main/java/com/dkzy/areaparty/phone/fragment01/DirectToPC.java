package com.dkzy.areaparty.phone.fragment01;

import android.util.Log;

import com.dkzy.areaparty.phone.AESc;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforMessageBean;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.JsonUitl;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.RequestFormat;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by FangXW on 2018/3/12.
 */

public class DirectToPC extends Thread {
    public static String PC_Ip;

    public DirectToPC(){
    }
    @Override
    public void run() {


        RequestFormat requestFormat = new RequestFormat();
        requestFormat.setName("PC");
        requestFormat.setCommand("AppConnectPCDirectly");
        requestFormat.setParam("");
        String data = JsonUitl.objectToString(requestFormat);
        Socket socket = null;
        try {
            Log.e("PC的IP",PC_Ip);
            socket = new  Socket(PC_Ip, IPAddressConst.PCDIRECTCONNECT_PORT);
            Log.e("pushPC", "直播消息" + data);
            OutputStream out = socket.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            int len;
            char[] rev = new char[1000];
            IPInforMessageBean receivedObject;
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            if((len = br.read(rev)) > 0) {
                String tempMessageStr = new String(rev, 0, len);
                try{
                    Log.e("pushTV", "接收到的消息" + tempMessageStr);
                    receivedObject = JsonUitl.stringToBean(tempMessageStr, IPInforMessageBean.class);
                    FillingIPInforList.parse(receivedObject);
                    FillingIPInforList.sendIPInfor();
                }catch (Exception e){}
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (socket != null && !socket.isClosed()) {
                IOUtils.closeQuietly(socket);
            }
        }


    }
}
