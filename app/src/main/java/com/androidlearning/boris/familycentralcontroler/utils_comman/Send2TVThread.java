package com.androidlearning.boris.familycentralcontroler.utils_comman;


import android.util.Log;

import com.androidlearning.boris.familycentralcontroler.utils_comman.jsonFormat.IPInforBean;
import com.androidlearning.boris.familycentralcontroler.myapplication.MyApplication;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by borispaul on 2016/12/23.
 */

public class Send2TVThread extends Thread{
    private static final int SOCKET_TIMEOUT = 5000;
    private String cmd;
    private final String tag = "Send2TVThread";

    public Send2TVThread(String cmd){
        this.cmd = cmd + "\n";
    }

    @Override
    public void run() {
        IPInforBean tvInfor = MyApplication.getSelectedTVIP();
        if(tvInfor != null && !tvInfor.ip.equals("")) {
            Socket client = new Socket();
            try {
                client.connect(new InetSocketAddress(tvInfor.ip, tvInfor.port), SOCKET_TIMEOUT);
                IOUtils.write(cmd, client.getOutputStream(), "UTF-8");
                Log.i(tag, cmd);
            } catch (IOException e) {
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
