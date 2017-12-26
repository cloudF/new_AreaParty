package com.dkzy.areaparty.phone.utils_comman;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.dkzy.areaparty.phone.IPAddressConst;
import com.dkzy.areaparty.phone.fragment02.vedioPlayControl;
import com.dkzy.areaparty.phone.model_comman.TVCommandItem;
import com.dkzy.areaparty.phone.myapplication.MyApplication;
import com.dkzy.areaparty.phone.myapplication.inforUtils.FillingIPInforList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by zhuyulin on 2017/9/25.
 */

public class ReceiveCommandFromTVPlayer extends Thread {
    public static boolean playerIsRun = false;
    private static final String tag = "CommandFromTVPlayer";
    public static String playerType = "VIDEO";
    @Override
    public void run() {
        //runReceiveMessage();
        runReceiveBroadCast();
    }

    private void runReceiveBroadCast() {
        byte[] buffer = new byte[1024];
        /*在这里同样使用约定好的端口*/
        DatagramSocket server = null;
        try{
            server = new DatagramSocket(IPAddressConst.REVEIVE_FROM_TVPLAYER_PORT);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            Log.w("CommandFromTVPlayer","开始监听");
            while(!FillingIPInforList.getCloseSingnal() && playerIsRun){
                server.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                Log.e(tag, "Get data from [" + packet.getAddress().toString() + "] : " +data);
                Log.e(tag, MyApplication.getSelectedTVIP().ip.toString());


                if(data!=null&&packet.getAddress().toString().contains(MyApplication.getSelectedTVIP().getIp().toString())){//过滤非当前连接TV的广播
                    Log.e(tag, "Execute cmd from tv");
//                    TVCommandItem tvCommandItem=null;
                    try {
                        TVCommandItem tvCommandItem=JSON.parseObject(data, TVCommandItem.class);
                        String ctrlCmd=tvCommandItem.getSecondcommand();
                        Log.w(tag,ctrlCmd);
                        switch (ctrlCmd){
                            case "PLAY_PAUSE":
                                String cmd=tvCommandItem.getFourthCommand();
                                switch (cmd){
                                    case "PLAY":
                                        vedioPlayControl.play();
                                        break;
                                    case "PAUSE":
                                        vedioPlayControl.pause();
                                        break;
                                }
                                break;
                            case "CHECK_PLAY_INFO":
                                Log.e(tag, tvCommandItem.getSevencommand());
                                Log.e(tag, tvCommandItem.getFourthCommand());
                                Log.e(tag, tvCommandItem.getFifthCommand());
                                playerType=tvCommandItem.getFirstcommand();
//
                                vedioPlayControl.checkPlayInfo(tvCommandItem.getFifthCommand(),tvCommandItem.getFourthCommand(),tvCommandItem.getSevencommand());
//
                                break;
                            case "PLAY_APPOINT_POSITION":
                                vedioPlayControl.playAppointPosition(tvCommandItem.getFifthCommand());
                                break;
                            case "EXIT_PLAYER":
                                //playerIsRun = false;
                                //修改为让遥控器一直可以开启
                                //   EventBus.getDefault().post(new TvPlayerChangeEvent(false), "tvPlayerStateChanged");
                                //vedioPlayControl.exit();
                                break;
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Log.w(tag,"Exception");
                    }

                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (server!=null){
                server.close();
            }
        }

    }

    public ReceiveCommandFromTVPlayer() {

    }

    public ReceiveCommandFromTVPlayer(boolean IsRun) {
        playerIsRun = IsRun;
    }

    /*private static void runReceiveMessage() {
        char[] rev = new char[1000];
        Log.w("CommandFromTVPlayer","开始监听");
        int len;
        try{
            ServerSocket servers = new ServerSocket(IPAddressConst.REVEIVE_FROM_TVPLAYER_PORT);

            while (!FillingIPInforList.getCloseSingnal() && playerIsRun){
                Socket s = servers.accept();
                BufferedReader temp = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf-8"));
                if((len = temp.read(rev)) > 0) {
                    String tempMessageStr = new String(rev, 0, len);
                    try{
                        Log.e("CommandFromTVPlayer", "接收到的消息" + tempMessageStr);
                        if (tempMessageStr.contains("success")){
                            playerIsRun = false;
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
                s.close();
            }
            servers.close();
            Log.w("CommandFromTVPlayer","线程结束");

        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    public void setPlayerIsRun(boolean IsRun) {
        playerIsRun = IsRun;
    }

    public static boolean getPlayerIsRun() {
        return playerIsRun;
    }
    public void stopThread(){
        playerIsRun = false;
        try {
            new Socket(FillingIPInforList.getIpStr(),IPAddressConst.REVEIVE_FROM_TVPLAYER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
