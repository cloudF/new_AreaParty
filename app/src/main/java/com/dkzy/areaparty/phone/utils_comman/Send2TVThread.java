package com.dkzy.areaparty.phone.utils_comman;


import android.util.Log;

import com.dkzy.areaparty.phone.OrderConst;
import com.dkzy.areaparty.phone.newAES;
import com.dkzy.areaparty.phone.utils_comman.jsonFormat.IPInforBean;
import com.dkzy.areaparty.phone.myapplication.MyApplication;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

import static com.dkzy.areaparty.phone.myapplication.MyApplication.context;
import static com.dkzy.areaparty.phone.myapplication.MyApplication.selectedTVIP;
import static com.dkzy.areaparty.phone.utils_comman.Send2SpecificTVThread.stringToMD5;

/**
 * Created by borispaul on 2016/12/23.
 */

public class Send2TVThread extends Thread{
    private static final int SOCKET_TIMEOUT = 5000;
    private String cmd;
    private final String tag = "Send2TVThread";
    public static String password = null;
    public static String pass = null;
    public static String pass1 = null;
    public Send2TVThread(String cmd){
        this.cmd = cmd;
    }

    @Override
    public void run() {
        IPInforBean tvInfor = MyApplication.getSelectedTVIP();
        if(tvInfor != null && !tvInfor.ip.equals("")) {
            Socket client = new Socket();
            try {
                password = new PreferenceUtil(context).read("TVMACS");
                HashMap<String, String> TVMacs = MyApplication.parse(password);
                pass=TVMacs.get(selectedTVIP.mac);
                pass1=stringToMD5(pass);
                String cmd1 = newAES.encrypt(cmd,pass1.getBytes());
                client.connect(new InetSocketAddress(tvInfor.ip, tvInfor.port), SOCKET_TIMEOUT);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                    writer.write(cmd1);


                writer.newLine();
                writer.flush();
                Log.i("dfafafdsfdfasfd", cmd1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (!client.isClosed()) {
                    IOUtils.closeQuietly(client);
                }
            }
        }

    }

}
